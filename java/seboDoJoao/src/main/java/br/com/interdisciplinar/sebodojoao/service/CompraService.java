package br.com.interdisciplinar.sebodojoao.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.interdisciplinar.sebodojoao.dto.CompraRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CompraResponseDTO;
import lombok.RequiredArgsConstructor;

// Aqui é onde a compra do site chega ao banco de dados.
//
// Em vez de gravar pedido, itens, pagamentos e entrega "na mão" (4 tabelas, vários passos),
// este serviço CHAMA AS PROCEDURES que criamos no SQL Server:
//   - sp_finalizar_compra : cria entrega + pedido + itens + parcelas do pagamento numa transação só
//                           (se algo falhar, nada fica pela metade) e a trigger baixa o estoque;
//   - sp_cancelar_pedido  : cancela o pedido e a trigger devolve o estoque;
//   - sp_avancar_entrega  : anda um passo na entrega (Pendente > Preparando > Em trânsito > Entregue).
// Para LER os pedidos usamos a view vw_pedidos_detalhados e a função fn_itens_do_pedido.
// As regras (estoque, parcelas, status...) ficam no banco; o Java só chama e traduz os erros.
@Service
@RequiredArgsConstructor
public class CompraService {
    private final JdbcTemplate jdbc;

    // ---------------------------------------------------------------------------------------
    // COMPRAR
    // ---------------------------------------------------------------------------------------
    public CompraResponseDTO finalizar(Long clienteId, CompraRequestDTO dto) {
        if (dto == null || dto.itens() == null || dto.itens().isEmpty()) {
            throw erro(HttpStatus.BAD_REQUEST, "O carrinho está vazio.");
        }

        // A procedure recebe os produtos como um texto JSON: [{"produtoId":1,"quantidade":2}, ...]
        // Só números entram nesse texto (Long e Integer), por isso não há risco de injeção de SQL.
        StringBuilder json = new StringBuilder("[");
        for (CompraRequestDTO.ItemCompraDTO item : dto.itens()) {
            if (item == null || item.produtoId() == null || item.quantidade() == null) {
                throw erro(HttpStatus.BAD_REQUEST, "Item do carrinho inválido.");
            }
            if (json.length() > 1) json.append(',');
            json.append("{\"produtoId\":").append(item.produtoId())
                .append(",\"quantidade\":").append(item.quantidade()).append('}');
        }
        json.append(']');

        String forma = dto.formaPagamento() == null ? "" : dto.formaPagamento().trim().toUpperCase();
        int parcelas = dto.parcelas() == null ? 1 : dto.parcelas();
        String transportadora = (dto.transportadora() == null || dto.transportadora().isBlank())
                ? "Correios (PAC)" : dto.transportadora().trim();
        // Texto muito grande não cabe na coluna (VARCHAR 100), então cortamos
        if (transportadora.length() > 100) transportadora = transportadora.substring(0, 100);
        int prazoDias = dto.prazoDias() == null ? 7 : Math.max(0, dto.prazoDias());
        BigDecimal frete = dto.valorFrete() == null ? BigDecimal.ZERO : dto.valorFrete();

        // O endereço de entrega é obrigatório (o banco confere se ele existe, está ativo e é deste cliente)
        if (dto.enderecoId() == null) {
            throw erro(HttpStatus.BAD_REQUEST, "Escolha um endereço de entrega.");
        }

        // Bandeira e final do cartão (só no pagamento com cartão). O final precisa ter exatamente 4 números.
        String bandeira = null;
        String finalCartao = null;
        if ("CARTAO_CREDITO".equals(forma)) {
            bandeira = dto.bandeira() == null || dto.bandeira().isBlank() ? null : dto.bandeira().trim().toLowerCase();
            finalCartao = dto.finalCartao() == null || dto.finalCartao().isBlank() ? null : dto.finalCartao().trim();
            if (bandeira != null && bandeira.length() > 30) bandeira = bandeira.substring(0, 30);
            if (finalCartao != null && !finalCartao.matches("\\d{4}")) {
                throw erro(HttpStatus.BAD_REQUEST, "Os 4 últimos dígitos do cartão são inválidos.");
            }
        }

        final String transportadoraFinal = transportadora;
        final String bandeiraFinal = bandeira;
        final String finalCartaoFinal = finalCartao;
        Integer pedidoId;
        try {
            // Chama: sp_finalizar_compra(cliente, ENDEREÇO, itens, forma, parcelas, transportadora, prazo, frete,
            //                            BANDEIRA, FINAL_CARTÃO, [saída] pedido_id)
            pedidoId = jdbc.execute(
                (java.sql.Connection conexao) -> {
                    try (CallableStatement chamada = conexao.prepareCall("{call dbo.sp_finalizar_compra(?,?,?,?,?,?,?,?,?,?,?)}")) {
                        chamada.setInt(1, clienteId.intValue());
                        chamada.setInt(2, dto.enderecoId().intValue());
                        chamada.setString(3, json.toString());
                        chamada.setString(4, forma);
                        chamada.setInt(5, parcelas);
                        chamada.setString(6, transportadoraFinal);
                        chamada.setInt(7, prazoDias);
                        chamada.setBigDecimal(8, frete);
                        chamada.setString(9, bandeiraFinal);       // null no Pix
                        chamada.setString(10, finalCartaoFinal);   // null no Pix
                        chamada.registerOutParameter(11, Types.INTEGER); // o banco devolve o ID do pedido criado
                        chamada.execute();
                        return chamada.getInt(11);
                    }
                });
        } catch (Exception e) {
            throw traduzirErroDoBanco(e);
        }

        return buscarPedido(pedidoId.longValue());
    }

    // ---------------------------------------------------------------------------------------
    // CANCELAR / AVANÇAR ENTREGA / PAGAR
    // ---------------------------------------------------------------------------------------
    public CompraResponseDTO cancelar(Long pedidoId, Long clienteId) {
        executarProcedure("{call dbo.sp_cancelar_pedido(?,?)}", pedidoId.intValue(), clienteId.intValue());
        return buscarPedido(pedidoId);
    }

    public CompraResponseDTO avancarEntrega(Long pedidoId) {
        executarProcedure("{call dbo.sp_avancar_entrega(?)}", pedidoId.intValue());
        return buscarPedido(pedidoId);
    }

    // Só para a demonstração: "faz de conta" que o banco confirmou o pagamento (Pix, por exemplo).
    // Marca as parcelas pendentes como aprovadas; a trigger tr_pagamentos_confirma_pedido então muda
    // o pedido de "Aguardando pagamento" para "Processando". clienteId = null quando é o funcionário.
    public CompraResponseDTO aprovarPagamento(Long pedidoId, Long clienteId) {
        CompraResponseDTO pedido = buscarPedido(pedidoId);
        if (clienteId != null && !clienteId.equals(pedido.clienteId())) {
            throw erro(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
        }
        if (!pedido.podePagar()) {
            throw erro(HttpStatus.CONFLICT, "Este pedido não tem pagamento pendente.");
        }

        jdbc.update("UPDATE pagamentos SET status = 2, data_pagamento = CAST(GETDATE() AS DATE) "
                + "WHERE pedido_id = ? AND status = 1", pedidoId);
        return buscarPedido(pedidoId);
    }

    private void executarProcedure(String chamadaSql, int... parametros) {
        try {
            jdbc.execute((java.sql.Connection conexao) -> {
                try (CallableStatement chamada = conexao.prepareCall(chamadaSql)) {
                    for (int i = 0; i < parametros.length; i++) chamada.setInt(i + 1, parametros[i]);
                    chamada.execute();
                    return null;
                }
            });
        } catch (Exception e) {
            throw traduzirErroDoBanco(e);
        }
    }

    // ---------------------------------------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------------------------------------
    public List<CompraResponseDTO> listarDoCliente(Long clienteId) {
        return montar(jdbc.queryForList(
            SELECT_PEDIDO + " WHERE cliente_id = ? ORDER BY pedido_id DESC", clienteId));
    }

    // Para o painel do funcionário: todos os pedidos da loja
    public List<CompraResponseDTO> listarTodos() {
        return montar(jdbc.queryForList(SELECT_PEDIDO + " ORDER BY pedido_id DESC"));
    }

    // Devolve o pedido só se ele for do cliente informado (um cliente não enxerga o pedido de outro)
    public CompraResponseDTO buscarDoCliente(Long pedidoId, Long clienteId) {
        CompraResponseDTO pedido = buscarPedido(pedidoId);
        if (!clienteId.equals(pedido.clienteId())) {
            throw erro(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
        }
        return pedido;
    }

    private CompraResponseDTO buscarPedido(Long pedidoId) {
        List<CompraResponseDTO> lista = montar(jdbc.queryForList(SELECT_PEDIDO + " WHERE pedido_id = ?", pedidoId));
        if (lista.isEmpty()) throw erro(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
        return lista.get(0);
    }

    // Lê a VIEW vw_pedidos_detalhados (pedido + cliente + atendente + entrega num SELECT só)
    private static final String SELECT_PEDIDO =
        "SELECT pedido_id, data, total, status_pedido, cliente_id, cliente, funcionario, "
        + "transportadora, codigo_rastreio, data_previsao_entrega, status_entrega, "
        // Colunas novas da view (endereço de entrega)
        + "destinatario, telefone_entrega, logradouro, numero_endereco, complemento, bairro, cep_numero, cidade, uf "
        + "FROM dbo.vw_pedidos_detalhados";

    private List<CompraResponseDTO> montar(List<Map<String, Object>> linhas) {
        List<CompraResponseDTO> pedidos = new ArrayList<>();
        for (Map<String, Object> l : linhas) {
            long id = ((Number) l.get("pedido_id")).longValue();

            // Itens do pedido: FUNÇÃO fn_itens_do_pedido (devolve uma "tabela" com produto, categoria, preço...)
            List<CompraResponseDTO.Item> itens = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;
            for (Map<String, Object> i : jdbc.queryForList(
                    "SELECT produto_id, produto, categoria, quantidade, preco_unitario, subtotal "
                    + "FROM dbo.fn_itens_do_pedido(?)", id)) {
                BigDecimal sub = (BigDecimal) i.get("subtotal");
                subtotal = subtotal.add(sub);
                itens.add(new CompraResponseDTO.Item(((Number) i.get("produto_id")).longValue(),
                    (String) i.get("produto"), (String) i.get("categoria"),
                    ((Number) i.get("quantidade")).intValue(), (BigDecimal) i.get("preco_unitario"), sub));
            }

            // Pagamento: junta as linhas (parcelas) da tabela pagamentos num resumo só
            List<Map<String, Object>> parcelas = jdbc.queryForList(
                "SELECT status, valor, quantidade_parcelas, forma_pagamento, bandeira, final_cartao FROM pagamentos "
                + "WHERE pedido_id = ? ORDER BY numero_parcela", id);
            CompraResponseDTO.Pagamento pagamento = null;
            boolean temPendente = false;
            if (!parcelas.isEmpty()) {
                boolean recusado = false;
                boolean estornado = false;
                for (Map<String, Object> p : parcelas) {
                    int st = ((Number) p.get("status")).intValue();
                    if (st == 1) temPendente = true;
                    if (st == 3) recusado = true;
                    if (st == 4) estornado = true;
                }
                String resumo = estornado ? "Estornado" : recusado ? "Recusado" : temPendente ? "Pendente" : "Aprovado";
                Map<String, Object> primeira = parcelas.get(0);
                pagamento = new CompraResponseDTO.Pagamento((String) primeira.get("forma_pagamento"),
                    ((Number) primeira.get("quantidade_parcelas")).intValue(), (BigDecimal) primeira.get("valor"), resumo,
                    (String) primeira.get("bandeira"), (String) primeira.get("final_cartao"));
            }

            String statusPedido = (String) l.get("status_pedido");
            String statusEntrega = (String) l.get("status_entrega");
            boolean andamento = "Aguardando pagamento".equals(statusPedido) || "Processando".equals(statusPedido);
            boolean entregaNaoSaiu = "Pendente".equals(statusEntrega) || "Preparando".equals(statusEntrega);

            BigDecimal total = (BigDecimal) l.get("total");
            Object previsao = l.get("data_previsao_entrega");
            CompraResponseDTO.Entrega entrega = new CompraResponseDTO.Entrega(
                (String) l.get("transportadora"), statusEntrega, (String) l.get("codigo_rastreio"),
                previsao == null ? null : previsao.toString());

            // Endereço de entrega, lido da view (que junta entregas, enderecos, ceps, cidades)
            CompraResponseDTO.Endereco endereco = new CompraResponseDTO.Endereco(
                (String) l.get("destinatario"), (String) l.get("telefone_entrega"), (String) l.get("logradouro"),
                (String) l.get("numero_endereco"), (String) l.get("complemento"), (String) l.get("bairro"),
                (String) l.get("cep_numero"), (String) l.get("cidade"), (String) l.get("uf"));

            pedidos.add(new CompraResponseDTO(id, String.format("SJ-%05d", id),
                l.get("data").toString().replace(' ', 'T'), total, subtotal, total.subtract(subtotal),
                statusPedido, ((Number) l.get("cliente_id")).longValue(), (String) l.get("cliente"),
                (String) l.get("funcionario"), entrega, endereco, pagamento, itens,
                andamento && entregaNaoSaiu,                                            // podeCancelar (igual à sp_cancelar_pedido)
                "Aguardando pagamento".equals(statusPedido) && temPendente,             // podePagar
                "Processando".equals(statusPedido) && ("Pendente".equals(statusEntrega) // podeAvancarEntrega (igual à sp_avancar_entrega)
                    || "Preparando".equals(statusEntrega) || "Em transito".equals(statusEntrega))));
        }
        return pedidos;
    }

    // ---------------------------------------------------------------------------------------
    // TRADUÇÃO DOS ERROS DO BANCO
    // ---------------------------------------------------------------------------------------
    // As procedures e triggers usam THROW com números (50001, 50003...). O driver devolve esse número
    // em SQLException.getErrorCode(). Aqui o número vira um código HTTP e a mensagem do banco
    // (que já está em português) é repassada para o site mostrar à pessoa.
    private ResponseStatusException traduzirErroDoBanco(Throwable erro) {
        for (Throwable t = erro; t != null; t = t.getCause()) {
            if (t instanceof ResponseStatusException r) return r;
            if (t instanceof SQLException sql && sql.getErrorCode() >= 50000 && sql.getErrorCode() < 51000) {
                HttpStatus status = switch (sql.getErrorCode()) {
                    case 50001 -> HttpStatus.UNAUTHORIZED;         // cliente não encontrado
                    case 50002, 50005, 50006, 50008, 50009 -> HttpStatus.BAD_REQUEST; // carrinho vazio, forma/parcelas/frete/endereço inválidos
                    case 50020 -> HttpStatus.NOT_FOUND;            // pedido não é deste cliente
                    default -> HttpStatus.CONFLICT;                // estoque, status do pedido/entrega...
                };
                return erro(status, sql.getMessage());
            }
        }
        // Qualquer outro problema é erro nosso (não vazamos detalhes do banco para a tela)
        erro.printStackTrace();
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível concluir a operação agora. Tente novamente.");
    }

    private static ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}

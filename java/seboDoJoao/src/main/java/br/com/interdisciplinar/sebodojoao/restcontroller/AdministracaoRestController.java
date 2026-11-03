package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// Rotas que só o funcionário administrador (funcionarios.admin = 1) pode usar:
// a gestão dos funcionários comuns e os números do painel de dados da loja.
// Toda rota começa conferindo, na sessão do servidor, se quem chamou é administrador.
@RestController
@RequestMapping("/api/administracao")
@RequiredArgsConstructor
public class AdministracaoRestController {
    private final JdbcTemplate jdbc;

    // Lista os funcionários comuns (admin = 0), ativos e inativos. A senha não é enviada.
    @GetMapping("/funcionarios")
    public List<Map<String, Object>> listarFuncionarios(HttpSession sessao) {
        ControleDeAcesso.exigirAdministrador(sessao);

        return jdbc.queryForList(
            "SELECT u.id, u.nome, u.email, u.telefone, f.codigo_funcionario AS codigoFuncionario, "
            + "       CASE u.status WHEN 1 THEN 'ATIVO' ELSE 'INATIVO' END AS status, "
            + "       CONVERT(VARCHAR(10), u.data_cadastro, 23) AS dataCadastro, "
            + "       (SELECT COUNT(*) FROM pedidos pe WHERE pe.funcionario_id = u.id) AS pedidosAtendidos, "
            + "       (SELECT COUNT(*) FROM produtos pr WHERE pr.funcionario_id = u.id) AS produtosCadastrados "
            + "FROM funcionarios f "
            + "INNER JOIN usuarios u ON u.id = f.usuario_id "
            + "WHERE f.admin = 0 "
            + "ORDER BY u.status, u.nome");
    }

    // "Apagar" funcionário: ele continua no banco (os pedidos e produtos dele guardam o histórico),
    // mas fica INATIVO (status 2) e não consegue mais entrar no sistema.
    @DeleteMapping("/funcionarios/{id}")
    public Map<String, String> inativarFuncionario(@PathVariable Long id, HttpSession sessao) {
        ControleDeAcesso.exigirAdministrador(sessao);
        mudarStatus(id, 2);
        return Map.of("mensagem", "Funcionário inativado.");
    }

    // Desfaz a inativação
    @PostMapping("/funcionarios/{id}/reativar")
    public Map<String, String> reativarFuncionario(@PathVariable Long id, HttpSession sessao) {
        ControleDeAcesso.exigirAdministrador(sessao);
        mudarStatus(id, 1);
        return Map.of("mensagem", "Funcionário reativado.");
    }

    // O "admin = 0" no comando garante que o administrador nunca é alterado por aqui
    private void mudarStatus(Long id, int novoStatus) {
        int alterados = jdbc.update(
            "UPDATE usuarios SET status = ? WHERE id = ? "
            + "AND id IN (SELECT usuario_id FROM funcionarios WHERE admin = 0)", novoStatus, id);

        if (alterados == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário comum não encontrado.");
        }
    }

    // Números do painel de dados. Pedido cancelado não conta como venda.
    @GetMapping("/painel")
    public Map<String, Object> painel(HttpSession sessao) {
        ControleDeAcesso.exigirAdministrador(sessao);

        Map<String, Object> painel = new LinkedHashMap<>();

        // Cartões de números
        Map<String, Object> numeros = new LinkedHashMap<>();
        numeros.put("vendas", jdbc.queryForObject("SELECT COUNT(*) FROM pedidos WHERE status <> 4", Integer.class));
        numeros.put("valorVendido", jdbc.queryForObject("SELECT COALESCE(SUM(total), 0) FROM pedidos WHERE status <> 4", java.math.BigDecimal.class));
        numeros.put("faturamento", jdbc.queryForObject("SELECT COALESCE(SUM(valor), 0) FROM pagamentos WHERE status = 2", java.math.BigDecimal.class));
        numeros.put("aReceber", jdbc.queryForObject(
            "SELECT COALESCE(SUM(pg.valor), 0) FROM pagamentos pg INNER JOIN pedidos pe ON pe.id = pg.pedido_id "
            + "WHERE pg.status = 1 AND pe.status <> 4", java.math.BigDecimal.class));
        numeros.put("estornado", jdbc.queryForObject("SELECT COALESCE(SUM(valor), 0) FROM pagamentos WHERE status = 4", java.math.BigDecimal.class));
        numeros.put("clientes", jdbc.queryForObject("SELECT COUNT(*) FROM clientes", Integer.class));
        numeros.put("clientesAtivos", jdbc.queryForObject(
            "SELECT COUNT(*) FROM clientes c INNER JOIN usuarios u ON u.id = c.usuario_id WHERE u.status = 1", Integer.class));
        numeros.put("funcionariosAtivos", jdbc.queryForObject(
            "SELECT COUNT(*) FROM funcionarios f INNER JOIN usuarios u ON u.id = f.usuario_id WHERE u.status = 1", Integer.class));
        numeros.put("produtosDisponiveis", jdbc.queryForObject("SELECT COUNT(*) FROM produtos WHERE status = 1", Integer.class));
        numeros.put("unidadesEmEstoque", jdbc.queryForObject("SELECT COALESCE(SUM(estoque), 0) FROM produtos WHERE status = 1", Integer.class));
        numeros.put("estoqueBaixo", jdbc.queryForObject("SELECT COUNT(*) FROM produtos WHERE status = 1 AND estoque <= 2", Integer.class));
        painel.put("numeros", numeros);

        // Pedidos em cada situação
        List<Map<String, Object>> porStatus = new ArrayList<>();
        for (Map<String, Object> linha : jdbc.queryForList("SELECT status, COUNT(*) AS quantidade FROM pedidos GROUP BY status ORDER BY status")) {
            int status = ((Number) linha.get("status")).intValue();
            String nome = switch (status) {
                case 1 -> "Aguardando pagamento";
                case 2 -> "Processando";
                case 3 -> "Concluído";
                default -> "Cancelado";
            };
            porStatus.add(Map.of("nome", nome, "quantidade", linha.get("quantidade")));
        }
        painel.put("pedidosPorStatus", porStatus);

        // Vendas dos últimos 30 dias com pedido (um ponto por dia)
        painel.put("vendasPorDia", jdbc.queryForList(
            "SELECT dia, quantidade, valor FROM ("
            + "  SELECT TOP 30 CONVERT(VARCHAR(10), data, 23) AS dia, COUNT(*) AS quantidade, SUM(total) AS valor "
            + "  FROM pedidos WHERE status <> 4 GROUP BY CONVERT(VARCHAR(10), data, 23) ORDER BY dia DESC"
            + ") ultimos ORDER BY dia"));

        // Quanto cada categoria vendeu (as sem venda aparecem com zero)
        painel.put("vendasPorCategoria", jdbc.queryForList(
            "SELECT c.nome, "
            + "  COALESCE(SUM(CASE WHEN pe.id IS NOT NULL THEN ip.quantidade ELSE 0 END), 0) AS unidades, "
            + "  COALESCE(SUM(CASE WHEN pe.id IS NOT NULL THEN ip.quantidade * ip.preco_unitario ELSE 0 END), 0) AS valor "
            + "FROM categorias c "
            + "LEFT JOIN produtos pr ON pr.categoria_id = c.id "
            + "LEFT JOIN item_pedidos ip ON ip.produto_id = pr.id "
            + "LEFT JOIN pedidos pe ON pe.id = ip.pedido_id AND pe.status <> 4 "
            + "GROUP BY c.id, c.nome ORDER BY c.id"));

        // Os 5 produtos mais vendidos
        painel.put("produtosMaisVendidos", jdbc.queryForList(
            "SELECT TOP 5 pr.nome, SUM(ip.quantidade) AS unidades "
            + "FROM item_pedidos ip "
            + "INNER JOIN pedidos pe ON pe.id = ip.pedido_id AND pe.status <> 4 "
            + "INNER JOIN produtos pr ON pr.id = ip.produto_id "
            + "GROUP BY pr.id, pr.nome ORDER BY SUM(ip.quantidade) DESC, pr.nome"));

        // Como os clientes pagam (conta um pagamento por pedido: a primeira parcela)
        painel.put("formasDePagamento", jdbc.queryForList(
            "SELECT pg.forma_pagamento AS nome, COUNT(*) AS quantidade "
            + "FROM pagamentos pg INNER JOIN pedidos pe ON pe.id = pg.pedido_id "
            + "WHERE pg.numero_parcela = 1 AND pe.status <> 4 "
            + "GROUP BY pg.forma_pagamento ORDER BY quantidade DESC"));

        return painel;
    }
}

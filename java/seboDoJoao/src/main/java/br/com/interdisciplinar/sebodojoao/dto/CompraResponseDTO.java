package br.com.interdisciplinar.sebodojoao.dto;

import java.math.BigDecimal;
import java.util.List;

// NOVO: o pedido completo, no formato que o site recebe do Java (usado em "Meus pedidos",
// na tela de pedido confirmado e no painel do funcionário). Os dados vêm das tabelas
// pedidos, item_pedidos, pagamentos e entregas, lidos pela view vw_pedidos_detalhados e pela
// função fn_itens_do_pedido. As datas viajam como texto (ex.: "2026-11-01T14:30:00").
public record CompraResponseDTO(
    Long id,
    String codigo,               // "SJ-00012", montado a partir do id do pedido
    String data,
    BigDecimal total,            // produtos + frete
    BigDecimal subtotalProdutos, // soma dos itens
    BigDecimal valorFrete,       // total - subtotalProdutos
    String statusPedido,         // Aguardando pagamento | Processando | Concluido | Cancelado
    Long clienteId,
    String cliente,
    String funcionario,          // atendente responsável pelo pedido
    Entrega entrega,
    Pagamento pagamento,
    List<Item> itens,
    // Dizem ao site quais botões mostrar (a regra de verdade continua nas procedures do banco)
    boolean podeCancelar,
    boolean podePagar,
    boolean podeAvancarEntrega
) {
    public record Item(Long produtoId, String produto, String categoria,
                       Integer quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {}

    // statusEntrega: Pendente | Preparando | Em transito | Entregue | Cancelado
    public record Entrega(String transportadora, String statusEntrega, String codigoRastreio, String previsao) {}

    // statusPagamento resume todas as parcelas: Pendente | Aprovado | Recusado
    public record Pagamento(String forma, int parcelas, BigDecimal valorParcela, String statusPagamento) {}
}

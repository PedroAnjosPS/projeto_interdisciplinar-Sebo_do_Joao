package br.com.interdisciplinar.sebodojoao.dto;

import java.math.BigDecimal;
import java.util.List;

// NOVO: é o "formulário" que o site (checkout) envia para o Java quando a pessoa confirma a compra.
// Repare no que NÃO vem aqui: o ID do cliente (o Java pega da sessão de login) e o preço dos
// produtos (o banco usa o preço cadastrado na tabela produtos). Assim o navegador não consegue
// comprar "em nome de outra pessoa" nem alterar valores.
public record CompraRequestDTO(
    // Produtos do carrinho: cada um com o id e a quantidade
    List<ItemCompraDTO> itens,
    // "PIX" ou "CARTAO_CREDITO" (o banco também aceita "BOLETO", mas o site ainda não oferece)
    String formaPagamento,
    // Quantidade de parcelas (1 a 3; só o cartão parcela). Se vier vazio, vale 1.
    Integer parcelas,
    // Nome da opção de entrega escolhida (ex.: "Econômico"). Vai para entregas.transportadora.
    String transportadora,
    // Prazo máximo da entrega, em dias. Vai para o cálculo de entregas.data_previsao_entrega.
    Integer prazoDias,
    // Valor do frete mostrado na tela (0 = grátis ou retirada). Entra no total do pedido.
    BigDecimal valorFrete,
    // NOVO: o endereço de entrega escolhido (id da tabela enderecos). O banco confere se ele é deste cliente.
    Long enderecoId,
    // NOVO: bandeira e 4 últimos dígitos do cartão usado (só no pagamento com cartão). O número
    // completo e o CVV nunca são enviados. Ficam gravados em pagamentos.bandeira e pagamentos.final_cartao.
    String bandeira,
    String finalCartao
) {
    // Um item do carrinho
    public record ItemCompraDTO(Long produtoId, Integer quantidade) {}
}

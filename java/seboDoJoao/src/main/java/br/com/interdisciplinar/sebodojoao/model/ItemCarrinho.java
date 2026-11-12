package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

// Representa a tabela "item_carrinhos": um produto no carrinho de um cliente.
// O preço unitário é o do produto na hora em que o item entrou (ou foi alterado) no carrinho
// Guardar um item no carrinho NÃO baixa o estoque (o estoque só baixa quando a compra é finalizada).
@Entity
@Table(name = "item_carrinhos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrinho {
    @EmbeddedId
    private ItemCarrinhoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clienteId")
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produtoId")
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal precoUnitario;
}
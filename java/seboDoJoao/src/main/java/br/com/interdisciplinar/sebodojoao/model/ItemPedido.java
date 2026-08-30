package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item_pedidos")
@Getter
@Setter
public class ItemPedido {
    // Atributos
    @EmbeddedId
    private ItemPedidoId id;

    @ManyToOne
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @MapsId("produtoId")
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    // Construtores
    public ItemPedido() {}

    // toString
    @Override
    public String toString() {
        return "\n\t{" +
                "\n\t\tId do pedido: " + pedido.getId() +
                "\n\t\tProduto: " + produto.getNome() +
                "\n\t\tQuantidade: " + quantidade +
                "\n\t\tPreço unitário: R$" + String.format("%.2f", precoUnitario) +
                "\n\t}";
    }
}

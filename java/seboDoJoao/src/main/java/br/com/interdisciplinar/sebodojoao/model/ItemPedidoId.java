package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ItemPedidoId implements Serializable {
    // Atributos
    @Column(name = "pedido_id")
    private Integer pedidoId;

    @Column(name = "produto_id")
    private Integer produtoId;

    // Construtores
    public ItemPedidoId() {}
}

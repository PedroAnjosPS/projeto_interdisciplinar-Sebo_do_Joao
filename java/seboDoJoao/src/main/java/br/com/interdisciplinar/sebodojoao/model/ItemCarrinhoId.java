package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

// A chave da tabela item_carrinhos é composta (cliente + produto): cada produto aparece
// uma única vez no carrinho de cada cliente (comprar mais unidades só aumenta a quantidade).
// Mesma ideia da ItemPedidoId.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemCarrinhoId implements Serializable {
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "produto_id")
    private Long produtoId;
}
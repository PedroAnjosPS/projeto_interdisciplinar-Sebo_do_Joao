package br.com.interdisciplinar.sebodojoao.model;

import lombok.Getter;

@Getter
public enum StatusPedido {
    PENDENTE(1), PROCESSANDO(2), ENVIADO(3), ENTREGUE(4), CANCELADO(5);

    private final int codigo;

    StatusPedido(int codigo) { this.codigo = codigo; }

    public static StatusPedido porCodigo(int codigo) {
        for (StatusPedido status : values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status do pedido inválido: " + codigo);
    }
}

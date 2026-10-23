package br.com.interdisciplinar.sebodojoao.model;

import lombok.Getter;

@Getter
public enum StatusEntrega {
    PENDENTE(1),
    PREPARANDO(2),
    EM_TRANSITO(3),
    ENTREGUE(4),
    CANCELADO(5);

    private final int codigo;

    StatusEntrega(int codigo) { this.codigo = codigo; }

    public static StatusEntrega porCodigo(int codigo) {
        for (StatusEntrega status : values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de entrega inválido: " + codigo);
    }
}

package br.com.interdisciplinar.sebodojoao.model;

import lombok.Getter;

@Getter
public enum StatusEntrega {
    PENDENTE(1),
    EM_PREPARACAO(2),
    ENVIADO(3),
    EM_TRANSITO(4),
    ENTREGUE(5),
    CANCELADO(6);

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

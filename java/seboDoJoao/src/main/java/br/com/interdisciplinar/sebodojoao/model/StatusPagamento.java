package br.com.interdisciplinar.sebodojoao.model;

import lombok.Getter;

@Getter
public enum StatusPagamento {
    PENDENTE(1),
    APROVADO(2),
    RECUSADO(3);

    private final int codigo;

    StatusPagamento(int codigo) { this.codigo = codigo; }

    public static StatusPagamento porCodigo(int codigo) {
        for (StatusPagamento status : values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de pagamento inválido: " + codigo);
    }
}

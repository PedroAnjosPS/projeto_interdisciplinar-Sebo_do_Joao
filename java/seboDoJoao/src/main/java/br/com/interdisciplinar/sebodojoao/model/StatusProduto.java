package br.com.interdisciplinar.sebodojoao.model;

import lombok.Getter;

@Getter
public enum StatusProduto {
    DISPONIVEL(1), INDISPONIVEL(2), ESGOTADO(3), DESATIVADO(4);

    private final int codigo;

    StatusProduto(int codigo) { this.codigo = codigo; }

    public static StatusProduto porCodigo(int codigo) {
        for (StatusProduto status : values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status do produto inválido: " + codigo);
    }
}

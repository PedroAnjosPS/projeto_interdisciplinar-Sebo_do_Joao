package br.com.interdisciplinar.sebodojoao.model;

import lombok.Getter;

@Getter
public enum StatusUsuario {
    ATIVO(1), INATIVO(2), BLOQUEADO(3), PENDENTE(4);

    private final int codigo;

    StatusUsuario(int codigo) { this.codigo = codigo; }

    public static StatusUsuario porCodigo(int codigo) {
        for (StatusUsuario status : values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status do usuário inválido: " + codigo);
    }
}

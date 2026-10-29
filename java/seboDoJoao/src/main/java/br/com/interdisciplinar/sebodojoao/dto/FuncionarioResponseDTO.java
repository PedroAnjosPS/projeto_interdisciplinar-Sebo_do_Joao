package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;

public record FuncionarioResponseDTO(
        Long id,
        String nome,
        String email,
        String senha,
        String telefone,
        String bairro,
        String logradouro,
        String cepNumero,
        StatusUsuario status,
        String fotoPerfil,
        String codigoFuncionario,
        Boolean admin
) { }

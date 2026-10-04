package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;

import java.time.LocalDate;

public record FuncionarioResponseDTO(
        Long id,
        String nome,
        String email,
        String senha,
        String telefone,
        String bairro,
        String logradouro,
        String cepNr,
        StatusUsuario status,
        String codigoFuncionario
) { }

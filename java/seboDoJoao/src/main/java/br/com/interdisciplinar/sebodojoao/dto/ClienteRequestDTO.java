package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;

import java.time.LocalDate;

public record ClienteRequestDTO(
        String nome,
        String email,
        String senha,
        String telefone,
        String bairro,
        String logradouro,
        String cepNr,
        StatusUsuario status,
        String cpf,
        LocalDate dataNascimento
) { }

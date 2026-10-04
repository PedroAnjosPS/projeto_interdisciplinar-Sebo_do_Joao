package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;

import java.time.LocalDate;

public record ClienteResponseDTO(
        Long id,
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

package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;

import java.time.LocalDate;

// O endereço não faz parte destes dados: ele fica na tabela enderecos
// (veja EnderecoRequestDTO e EnderecoResponseDTO).
public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        String senha,
        String telefone,
        StatusUsuario status,
        String fotoPerfil,
        String cpf,
        LocalDate dataNascimento
) { }

package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;

// ALTERADO: os campos bairro, logradouro e cepNumero foram removidos: o endereço agora fica na tabela
// enderecos (veja EnderecoRequestDTO e EnderecoResponseDTO), e não mais junto dos dados da conta.
public record FuncionarioResponseDTO(
        Long id,
        String nome,
        String email,
        String senha,
        String telefone,
        StatusUsuario status,
        String fotoPerfil,
        String codigoFuncionario,
        Boolean admin
) { }

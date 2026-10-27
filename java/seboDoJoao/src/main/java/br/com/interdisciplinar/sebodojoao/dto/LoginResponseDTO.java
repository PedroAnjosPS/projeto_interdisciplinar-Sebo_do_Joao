package br.com.interdisciplinar.sebodojoao.dto;

public record LoginResponseDTO(
    Long id,
    String nome,
    String email,
    String perfil
) {}

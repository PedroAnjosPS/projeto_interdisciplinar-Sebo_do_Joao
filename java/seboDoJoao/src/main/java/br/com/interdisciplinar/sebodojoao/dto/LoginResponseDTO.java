package br.com.interdisciplinar.sebodojoao.dto;

// fotoPerfil: sem este campo, a foto sumia do cabeçalho a cada novo login,
// mesmo estando gravada no banco.
public record LoginResponseDTO(
    Long id,
    String nome,
    String email,
    String perfil,
    String fotoPerfil
) {}

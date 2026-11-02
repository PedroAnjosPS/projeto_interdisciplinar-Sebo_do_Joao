package br.com.interdisciplinar.sebodojoao.dto;

// NOVO: um cartão salvo, no formato que o site recebe do Java.
public record CartaoResponseDTO(
        Long id,
        String bandeira,
        String finalNumero,
        String titular,
        String validade,
        boolean padrao
) { }

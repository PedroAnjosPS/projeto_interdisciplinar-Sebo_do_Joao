package br.com.interdisciplinar.sebodojoao.dto;

// O que o site envia para salvar um cartão. O número completo e o CVV NUNCA são enviados:
// o navegador confere o número na hora e manda só a bandeira e os 4 últimos dígitos.
public record CartaoRequestDTO(
        String bandeira,       // visa, mastercard, amex, elo, hipercard ou diners
        String finalNumero,    // 4 últimos dígitos
        String titular,
        String validade,       // MM/AA
        Boolean padrao
) { }

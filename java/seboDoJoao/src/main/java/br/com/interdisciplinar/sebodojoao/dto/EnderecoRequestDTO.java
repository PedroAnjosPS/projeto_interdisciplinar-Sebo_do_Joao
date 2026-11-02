package br.com.interdisciplinar.sebodojoao.dto;

// O "formulário" que o site envia para salvar um endereço (criar ou editar).
// Repare no que NÃO vem aqui: o ID do cliente (o Java pega da sessão de login).
// "cidade" e "uf" vêm como texto; o Java procura (ou cria) a UF, a cidade e o CEP nas tabelas do banco.
public record EnderecoRequestDTO(
        String apelido,        // "Casa", "Trabalho"... (vazio = "Endereço")
        String cep,            // "15010-000" ou "15010000"
        String logradouro,     // rua ou avenida
        String numero,         // vazio quando "semNumero" for true
        Boolean semNumero,
        String complemento,
        String bairro,
        String cidade,
        String uf,             // sigla, ex.: "SP"
        Boolean padrao         // true = deixar como endereço padrão
) { }

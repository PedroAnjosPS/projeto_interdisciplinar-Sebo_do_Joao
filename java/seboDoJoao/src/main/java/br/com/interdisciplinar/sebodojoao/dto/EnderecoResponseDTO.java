package br.com.interdisciplinar.sebodojoao.dto;

// NOVO: um endereço no formato que o site recebe do Java. A cidade e a UF vêm das tabelas
// ceps -> cidades -> ufs (o endereço guarda só o número do CEP).
public record EnderecoResponseDTO(
        Long id,
        String apelido,
        String cep,
        String logradouro,
        String numero,         // null = "Sem número"
        String complemento,
        String bairro,
        String cidade,
        String uf,
        boolean padrao
) { }

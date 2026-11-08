package br.com.interdisciplinar.sebodojoao.dto;

// Um item do carrinho (usado tanto para enviar quanto para receber). O carrinho guarda só
// "qual produto" e "quantas unidades"; nome, preço e imagem o site busca na lista de produtos.
public record ItemCarrinhoDTO(Long produtoId, Integer quantidade) { }

package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusProduto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer estoque,
        Long categoriaId,
        Long generoId,
        Long funcionarioId,
        StatusProduto status
) { }

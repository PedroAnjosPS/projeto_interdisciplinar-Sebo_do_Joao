package br.com.interdisciplinar.sebodojoao.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        ItemPedidoIdDTO id,
        Long pedidoId,
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario
) { }

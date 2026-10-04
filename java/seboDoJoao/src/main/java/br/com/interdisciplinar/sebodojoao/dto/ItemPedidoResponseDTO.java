package br.com.interdisciplinar.sebodojoao.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long pedidoId,
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario
) { }

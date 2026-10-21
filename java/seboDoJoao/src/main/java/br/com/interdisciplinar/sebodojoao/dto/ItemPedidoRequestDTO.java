package br.com.interdisciplinar.sebodojoao.dto;

import java.math.BigDecimal;

public record ItemPedidoRequestDTO(
        Long pedidoId,
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario
) { }

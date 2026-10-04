package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemPedidoRequestDTO(
        Long pedidoId,
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario
) { }

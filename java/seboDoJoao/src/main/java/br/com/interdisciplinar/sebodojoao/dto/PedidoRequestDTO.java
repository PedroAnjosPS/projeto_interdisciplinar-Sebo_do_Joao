package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoRequestDTO(
        LocalDateTime data,
        BigDecimal total,
        StatusPedido status,
        Long clienteId,
        Long funcionarioId,
        Long entregaId
) { }

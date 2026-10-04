package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusPagamento;
import br.com.interdisciplinar.sebodojoao.model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagamentoRequestDTO(
        Integer numeroParcela,
        StatusPagamento status,
        BigDecimal valor,
        Integer quantidadeParcelas,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        Long pedidoId
) { }

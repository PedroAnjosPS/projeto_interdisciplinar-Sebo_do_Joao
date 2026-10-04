package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoResponseDTO(
        Long id,
        Integer numeroParcela,
        StatusPagamento status,
        BigDecimal valor,
        Integer quantidadeParcelas,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        Long pedidoId
) { }

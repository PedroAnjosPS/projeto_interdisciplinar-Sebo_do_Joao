package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusEntrega;
import br.com.interdisciplinar.sebodojoao.model.StatusProduto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EntregaRequestDTO(
        String transportadora,
        String linkRastreio,
        String codigoRastreio,
        LocalDateTime dataPostagem,
        LocalDate dataPrevisaoEntrega,
        LocalDateTime dataEntrega,
        StatusEntrega status
) { }

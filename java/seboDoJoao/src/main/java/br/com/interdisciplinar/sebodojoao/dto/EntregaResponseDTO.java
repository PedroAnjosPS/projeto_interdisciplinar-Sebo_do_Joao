package br.com.interdisciplinar.sebodojoao.dto;

import br.com.interdisciplinar.sebodojoao.model.StatusEntrega;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EntregaResponseDTO(
        Long id,
        String transportadora,
        String linkRastreio,
        String codigoRastreio,
        LocalDateTime dataPostagem,
        LocalDate dataPrevisaoEntrega,
        LocalDateTime dataEntrega,
        StatusEntrega status
) { }

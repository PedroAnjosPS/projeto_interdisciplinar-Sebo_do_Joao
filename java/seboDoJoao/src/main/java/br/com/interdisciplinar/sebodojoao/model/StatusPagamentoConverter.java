package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusPagamentoConverter implements AttributeConverter<StatusPagamento, Integer> {
    @Override
    public Integer convertToDatabaseColumn(StatusPagamento status) {
        return status == null ? null : status.getCodigo();
    }

    @Override
    public StatusPagamento convertToEntityAttribute(Integer codigo) {
        return codigo == null ? null : StatusPagamento.porCodigo(codigo);
    }
}

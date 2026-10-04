package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusEntregaConverter implements AttributeConverter<StatusEntrega, Integer> {
    @Override
    public Integer convertToDatabaseColumn(StatusEntrega status) {
        return status == null ? null : status.getCodigo();
    }

    @Override
    public StatusEntrega convertToEntityAttribute(Integer codigo) {
        return codigo == null ? null : StatusEntrega.porCodigo(codigo);
    }
}

package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusPedidoConverter implements AttributeConverter<StatusPedido, Integer> {
    @Override
    public Integer convertToDatabaseColumn(StatusPedido status) {
        return status == null ? null : status.getCodigo();
    }

    @Override
    public StatusPedido convertToEntityAttribute(Integer codigo) {
        return codigo == null ? null : StatusPedido.porCodigo(codigo);
    }
}

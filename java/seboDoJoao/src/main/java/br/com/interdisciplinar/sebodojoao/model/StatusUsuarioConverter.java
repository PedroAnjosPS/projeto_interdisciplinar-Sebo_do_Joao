package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusUsuarioConverter implements AttributeConverter<StatusUsuario, Integer> {
    @Override
    public Integer convertToDatabaseColumn(StatusUsuario status) {
        return status == null ? null : status.getCodigo();
    }

    @Override
    public StatusUsuario convertToEntityAttribute(Integer codigo) {
        return codigo == null ? null : StatusUsuario.porCodigo(codigo);
    }
}

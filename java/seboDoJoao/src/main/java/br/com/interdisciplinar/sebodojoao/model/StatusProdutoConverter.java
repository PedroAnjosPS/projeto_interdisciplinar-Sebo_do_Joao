package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusProdutoConverter implements AttributeConverter<StatusProduto, Integer> {
    @Override
    public Integer convertToDatabaseColumn(StatusProduto status) {
        return status == null ? null : status.getCodigo();
    }

    @Override
    public StatusProduto convertToEntityAttribute(Integer codigo) {
        return codigo == null ? null : StatusProduto.porCodigo(codigo);
    }
}

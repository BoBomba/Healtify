package com.healtify.healtify.models.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Zapisuje liste objawy w jednej kolumnie TEXT, po jednym na linie.
 * Dzieki temu wpisy dziennika mieszcza sie w JEDNEJ tabeli, bez tabeli pomocniczej.
 * Znaki nowej linii w elementach sa zamieniane na spacje, zeby nie rozjechac separatora.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SEPARATOR = "\n";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        List<String> cleaned = new ArrayList<>();
        for (String value : attribute) {
            if (value == null) {
                continue;
            }
            String single = value.replaceAll("\\R", " ").trim();
            if (!single.isEmpty()) {
                cleaned.add(single);
            }
        }
        return cleaned.isEmpty() ? null : String.join(SEPARATOR, cleaned);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(dbData.split(SEPARATOR)));
    }
}

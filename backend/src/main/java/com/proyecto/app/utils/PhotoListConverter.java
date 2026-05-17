package com.proyecto.app.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.app.common.Photo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class PhotoListConverter implements AttributeConverter<List<Photo>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Photo> photos) {
        try {
            return objectMapper.writeValueAsString(photos);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<Photo> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Photo>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
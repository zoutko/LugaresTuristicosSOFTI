package com.proyecto.app.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class PhotoListConverter implements AttributeConverter<List<com.proyecto.app.touristPlaceManagment.domain.Photo>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<com.proyecto.app.touristPlaceManagment.domain.Photo> photos) {
        try {
            return objectMapper.writeValueAsString(photos);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<com.proyecto.app.touristPlaceManagment.domain.Photo> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<com.proyecto.app.touristPlaceManagment.domain.Photo>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
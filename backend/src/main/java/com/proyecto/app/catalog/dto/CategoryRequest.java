package com.proyecto.app.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

    @NotBlank(message = "El nombre de la categoria es obligatorio")
    private String name;

    public CategoryRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


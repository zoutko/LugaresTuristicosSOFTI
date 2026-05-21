package com.proyecto.app.touristPlaceManagment.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.proyecto.app.common.Environment;
import com.proyecto.app.common.Location;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO de entrada para crear o actualizar un TouristPlace.
 * No contiene Album ni fotos; eso se gestiona via /media.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristPlaceRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;
    private String cancelationPolicy;
    private String duration;
    private Environment environment;
    private Location location;
    private List<Long> categoryIds = new ArrayList<>(); // IDs de categorías existentes
}

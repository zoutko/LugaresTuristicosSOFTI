package com.proyecto.app.touristPlaceManagment.dto.response;

import com.proyecto.app.common.Environment;
import com.proyecto.app.common.Location;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * DTO de salida para TouristPlace.
 * Incluye resumen del álbum (totalPhotos) pero no expone la lista interna.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristPlaceResponse {

    private UUID id;
    private String name;
    private String description;
    private String duration;
    private Environment environment;
    private Location location;
    private List<ActivityResponse> activities;
    private List<String> categories;

    private int totalPhotos;
}

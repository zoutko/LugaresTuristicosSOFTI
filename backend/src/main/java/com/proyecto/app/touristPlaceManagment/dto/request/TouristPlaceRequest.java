package com.proyecto.app.touristPlaceManagment.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.proyecto.app.common.Environment;
import com.proyecto.app.common.Location;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristPlaceRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;
    private String duration;
    private Environment environment;
    private Location location;
    private List<UUID> categoryIds = new ArrayList<>(); 
}

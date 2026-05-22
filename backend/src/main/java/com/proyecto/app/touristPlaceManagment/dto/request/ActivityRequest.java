package com.proyecto.app.touristPlaceManagment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequest {

    @NotBlank(message = "La descripción de la actividad es obligatoria")
    private String description;
}

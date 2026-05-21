package com.proyecto.app.media.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoRequest {

    @NotBlank(message = "El filePath no puede estar vacío")
    private String filePath;

    private String description;
}

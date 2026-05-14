package com.proyecto.app.media.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoResponse {

    private String filePath;
    private String fileName;
    private String description;
}

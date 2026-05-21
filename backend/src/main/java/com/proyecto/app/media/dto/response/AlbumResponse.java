package com.proyecto.app.media.dto.response;

import lombok.*;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumResponse {

    private int currentIndex;
    private int totalPhotos;
    private PhotoResponse currentPhoto;
    private List<PhotoResponse> photos;
}

package com.proyecto.app.touristPlaceManagment.dto.response;

import com.proyecto.app.common.Environment;
import com.proyecto.app.common.Location;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristPlaceResponse {

    private Long id;
    private String name;
    private String description;
    private String duration;
    private Environment environment;
    private Location location;
    private List<ActivityResponse> activities;
    private List<String> categories;

    private int totalPhotos;
}

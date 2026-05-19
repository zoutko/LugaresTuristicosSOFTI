package com.proyecto.app.tourManagment.dto.request;

import com.proyecto.app.common.Location;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTourRequest {

    private String name;
    private List<Long> categoryIds;
    private String environment;        // "INTERIOR", "MIXED", "EXTERIOR"
    private String description;
    private String recommendations;
    private Double price;
    private Location location;
    private Location meetingPoint;
    private List<Long> itineraryPlaceIds;

}

package com.proyecto.app.tourManagment.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTourRequest {

    private String name;
    private List<Long> categoryIds;
    private Long enviromentId;
    private String description;
    private String recommendations;
    private Double price;
    private Long locationId;
    private Long meetingPointId;

 }

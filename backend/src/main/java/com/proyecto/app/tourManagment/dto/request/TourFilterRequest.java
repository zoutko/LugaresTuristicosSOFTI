package com.proyecto.app.tourManagment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TourFilterRequest {
    private String name;
    private List<String> environments;   // ["INTERIOR", "EXTERIOR", "MIXED"]
    private List<Long> categoryIds;
    private Double maxPrice;
}

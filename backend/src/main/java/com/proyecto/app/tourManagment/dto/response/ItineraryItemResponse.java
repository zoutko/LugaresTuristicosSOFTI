package com.proyecto.app.tourManagment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItineraryItemResponse {
    private Long itineraryId;
    private int position;
    private Long touristPlaceId;
    private String touristPlaceName;


    public ItineraryItemResponse(Long itineraryId, int position, Long touristPlaceId, String touristPlaceName) {
        this.itineraryId = itineraryId;
        this.position = position;
        this.touristPlaceId = touristPlaceId;
        this.touristPlaceName = touristPlaceName;
    }
}
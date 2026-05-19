package com.proyecto.app.tourManagment.dto.response;

import java.util.List;

import com.proyecto.app.media.dto.response.AlbumResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TourResponse {

    private Long id;
    private String name;
    private List<String> categories;
    private String environment;
    private String description;
    private String recommendations;
    private double price;
    private String location;       // getFullLocation()
    private String meetingPoint;   // getFullLocation()
    private List<ItineraryItemResponse> itinerary;
    private TourOfferResponse tourOffer;
    private AlbumResponse album;
}

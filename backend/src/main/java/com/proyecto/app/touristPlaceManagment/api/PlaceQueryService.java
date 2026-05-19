package com.proyecto.app.touristPlaceManagment.api;

import java.util.List;

import com.proyecto.app.common.Environment;
import com.proyecto.app.touristPlaceManagment.dto.response.TouristPlaceResponse;

public interface PlaceQueryService {
    TouristPlaceResponse findById(Long placeId);
    boolean exists(Long placeId);
    List<TouristPlaceResponse> findByIds(List<Long> placeIds);
    Environment getEnvironment(Long placeId);

}

// touristPlaceManagment/service/PlaceQueryServiceImpl.java
package com.proyecto.app.touristPlaceManagment.service;

import com.proyecto.app.common.Environment;
import com.proyecto.app.touristPlaceManagment.api.PlaceQueryService;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.touristPlaceManagment.dto.response.TouristPlaceResponse;
import com.proyecto.app.touristPlaceManagment.exception.TouristPlaceNotFoundException;
import com.proyecto.app.touristPlaceManagment.repository.TouristPlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceQueryServiceImpl implements PlaceQueryService {

    private final TouristPlaceRepository placeRepository;
    private final TouristPlaceService touristPlaceService;

    public PlaceQueryServiceImpl(TouristPlaceRepository placeRepository,
                                  TouristPlaceService touristPlaceService) {
        this.placeRepository = placeRepository;
        this.touristPlaceService = touristPlaceService;
    }

    @Override
    public TouristPlaceResponse findById(Long placeId) {
        return touristPlaceService.getById(placeId);
    }

    @Override
    public List<TouristPlaceResponse> findAllByIds(List<Long> placeIds) {
        return placeIds.stream()
                .map(touristPlaceService::getById)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(Long placeId) {
        return placeRepository.existsById(placeId);
    }

    @Override
    public Environment getEnvironment(Long placeId) {
        return placeRepository.findById(placeId)
                .map(TouristPlace::getEnvironment)
                .orElseThrow(() -> new TouristPlaceNotFoundException(placeId.toString()));
    }
}
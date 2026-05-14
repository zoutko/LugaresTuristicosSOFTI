package com.proyecto.app.touristPlaceManagment.service;

import com.proyecto.app.common.Environment;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.touristPlaceManagment.dto.request.TouristPlaceRequest;
import com.proyecto.app.touristPlaceManagment.dto.response.ActivityResponse;
import com.proyecto.app.touristPlaceManagment.dto.response.TouristPlaceResponse;
import com.proyecto.app.touristPlaceManagment.exception.InvalidPlaceDataException;
import com.proyecto.app.touristPlaceManagment.exception.TouristPlaceNotFoundException;
import com.proyecto.app.touristPlaceManagment.repository.TouristPlaceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TouristPlaceService {

    private final TouristPlaceRepository placeRepository;

    public TouristPlaceService(TouristPlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }


    public List<TouristPlaceResponse> getAll() {
        return placeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TouristPlaceResponse getById(UUID id) {
        return toResponse(resolveOrThrow(id));
    }

    public List<TouristPlaceResponse> getByName(String name) {
        return placeRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TouristPlaceResponse> getByCity(String city) {
        return placeRepository.findByLocationCityIgnoreCase(city).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TouristPlaceResponse> getByEnvironment(Environment environment) {
        return placeRepository.findByEnvironment(environment).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public TouristPlaceResponse create(TouristPlaceRequest request) {
        validateRequest(request);
        TouristPlace place = toEntity(request);
        return toResponse(placeRepository.save(place));
    }

    @Transactional
    public TouristPlaceResponse update(UUID id, TouristPlaceRequest request) {
        validateRequest(request);
        TouristPlace existing = resolveOrThrow(id);
        applyUpdate(existing, request);
        return toResponse(placeRepository.save(existing));
    }

    @Transactional
    public void delete(UUID id) {
        TouristPlace place = resolveOrThrow(id);
        placeRepository.delete(place);
    }

    TouristPlace resolveOrThrow(UUID id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new TouristPlaceNotFoundException(id.toString()));
    }

    private void validateRequest(TouristPlaceRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new InvalidPlaceDataException("El nombre del lugar no puede estar vacío");
        }
    }

    private TouristPlace toEntity(TouristPlaceRequest req) {
        TouristPlace p = new TouristPlace();
        applyUpdate(p, req);
        return p;
    }

    private void applyUpdate(TouristPlace p, TouristPlaceRequest req) {
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setCancelationPolicy(req.getCancelationPolicy());
        p.setDuration(req.getDuration());
        p.setEnvironment(req.getEnvironment());
        p.setLocation(req.getLocation());
    }

    private TouristPlaceResponse toResponse(TouristPlace p) {
        List<ActivityResponse> activityResponses = p.getActivities() == null
                ? List.of()
                : p.getActivities().stream()
                        .map(a -> new ActivityResponse(a.getId(), a.getDescription()))
                        .collect(Collectors.toList());

        return TouristPlaceResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .cancelationPolicy(p.getCancelationPolicy())
                .duration(p.getDuration())
                .environment(p.getEnvironment())
                .location(p.getLocation())
                .activities(activityResponses)
                .totalPhotos(p.getAlbum() != null ? p.getAlbum().getPhotos().size() : 0)
                .build();
    }
}

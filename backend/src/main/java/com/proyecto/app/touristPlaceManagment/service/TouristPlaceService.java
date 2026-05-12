package com.proyecto.app.touristPlaceManagment.service;

import com.proyecto.app.touristPlaceManagment.domain.Activity;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.common.Category;
import com.proyecto.app.common.Environment;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TouristPlaceService {

    private List<TouristPlace> places = new ArrayList<>();


    public List<TouristPlace> getAll() {
        return places;
    }


    public TouristPlace getById(UUID id) {
        return places.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Place not found: " + id));
    }


    public TouristPlace create(TouristPlace place) {
        place.setId(UUID.randomUUID());
        places.add(place);
        return place;
    }


    public TouristPlace update(UUID id, TouristPlace updated) {
        TouristPlace existing = getById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCancelationPolicy(updated.getCancelationPolicy());
        existing.setDuration(updated.getDuration());
        existing.setEnvironment(updated.getEnvironment());
        existing.setLocation(updated.getLocation());
        existing.setAlbum(updated.getAlbum());
        existing.setCategories(updated.getCategories());
        existing.setActivities(updated.getActivities());
        return existing;
    }


    public void delete(UUID id) {
        TouristPlace existing = getById(id);
        places.remove(existing);
    }


    public List<TouristPlace> getByName(String name) {
        return places.stream()
            .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
    }


    public List<TouristPlace> getByCity(String city) {
        return places.stream()
            .filter(p -> p.getLocation().getCity().equalsIgnoreCase(city))
            .collect(Collectors.toList());
    }


    public List<TouristPlace> getByEnvironment(Environment environment) {
        return places.stream()
            .filter(p -> p.getEnvironment().equals(environment))
            .collect(Collectors.toList());
    }


    public List<TouristPlace> getByCategory(Long categoryId) {
        return places.stream()
            .filter(p -> p.getCategories().stream()
                .anyMatch(c -> c.getId().equals(categoryId)))
            .collect(Collectors.toList());
    }


    public TouristPlace addActivity(UUID placeId, Activity activity) {
        TouristPlace place = getById(placeId);
        place.addActivity(activity);
        return place;
    }


    public TouristPlace removeActivity(UUID placeId, Activity activity) {
        TouristPlace place = getById(placeId);
        place.removeActivity(activity);
        return place;
    }


    public TouristPlace addCategory(UUID placeId, Category category) {
        TouristPlace place = getById(placeId);
        place.addCategory(category);
        return place;
    }


    public TouristPlace removeCategory(UUID placeId, Category category) {
        TouristPlace place = getById(placeId);
        place.removeCategory(category);
        return place;
    }
}

package com.proyecto.test.touristPlaceManagment.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.test.touristPlaceManagment.domain.Activity;
import com.proyecto.test.touristPlaceManagment.domain.TouristPlace;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private TouristPlaceService touristPlaceService;

    // Traer todas las actividades de un lugar
    public List<Activity> getActivitiesByPlace(UUID placeId) {
        return touristPlaceService.getById(placeId).getActivities();
    }

    // Agregar actividad a un lugar
    public TouristPlace addActivity(UUID placeId, Activity activity) {
        return touristPlaceService.addActivity(placeId, activity);
    }

    // Eliminar actividad de un lugar
    public TouristPlace removeActivity(UUID placeId, Activity activity) {
        return touristPlaceService.removeActivity(placeId, activity);
    }

    // Buscar actividad por id dentro de un lugar
    public Activity getActivityById(UUID placeId, int activityId) {
        return touristPlaceService.getById(placeId).getActivities().stream()
            .filter(a -> a.getId() == activityId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));
    }

    // Actualizar descripcion de una actividad
    public Activity updateDescription(UUID placeId, int activityId, String description) {
        Activity activity = getActivityById(placeId, activityId);
        activity.setDescription(description);
        return activity;
    }
}


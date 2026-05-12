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


    public List<Activity> getActivitiesByPlace(UUID placeId) {
        return touristPlaceService.getById(placeId).getActivities();
    }


    public TouristPlace addActivity(UUID placeId, Activity activity) {
        return touristPlaceService.addActivity(placeId, activity);
    }


    public TouristPlace removeActivity(UUID placeId, Activity activity) {
        return touristPlaceService.removeActivity(placeId, activity);
    }


    public Activity getActivityById(UUID placeId, int activityId) {
        return touristPlaceService.getById(placeId).getActivities().stream()
            .filter(a -> a.getId() == activityId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));
    }


    public Activity updateDescription(UUID placeId, int activityId, String description) {
        Activity activity = getActivityById(placeId, activityId);
        activity.setDescription(description);
        return activity;
    }
}


package com.proyecto.app.touristPlaceManagment.service;

import com.proyecto.app.touristPlaceManagment.domain.Activity;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.touristPlaceManagment.dto.request.ActivityRequest;
import com.proyecto.app.touristPlaceManagment.dto.response.ActivityResponse;
import com.proyecto.app.touristPlaceManagment.dto.response.TouristPlaceResponse;
import com.proyecto.app.touristPlaceManagment.exception.ActivityNotFoundException;
import com.proyecto.app.touristPlaceManagment.repository.TouristPlaceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final TouristPlaceService touristPlaceService;
    private final TouristPlaceRepository placeRepository;

    public ActivityService(TouristPlaceService touristPlaceService,
                           TouristPlaceRepository placeRepository) {
        this.touristPlaceService = touristPlaceService;
        this.placeRepository = placeRepository;
    }

    public List<ActivityResponse> getActivitiesByPlace(Long placeId) {
        return touristPlaceService.resolveOrThrow(placeId).getActivities()
                .stream()
                .map(a -> new ActivityResponse(a.getId(), a.getDescription()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TouristPlaceResponse addActivity(Long placeId, ActivityRequest request) {
        TouristPlace place = touristPlaceService.resolveOrThrow(placeId);
        Activity activity = new Activity();
        activity.setDescription(request.getDescription());
        place.addActivity(activity);
        placeRepository.save(place);
        return touristPlaceService.getById(placeId);
    }

    @Transactional
    public TouristPlaceResponse removeActivity(Long placeId, int activityId) {
        TouristPlace place = touristPlaceService.resolveOrThrow(placeId);
        Activity activity = resolveActivityOrThrow(place, activityId);
        place.removeActivity(activity);
        placeRepository.save(place);
        return touristPlaceService.getById(placeId);
    }

    public ActivityResponse getActivityById(Long placeId, int activityId) {
        TouristPlace place = touristPlaceService.resolveOrThrow(placeId);
        Activity a = resolveActivityOrThrow(place, activityId);
        return new ActivityResponse(a.getId(), a.getDescription());
    }


    private Activity resolveActivityOrThrow(TouristPlace place, int activityId) {
        return place.getActivities().stream()
                .filter(a -> a.getId() == activityId)
                .findFirst()
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
    }
}

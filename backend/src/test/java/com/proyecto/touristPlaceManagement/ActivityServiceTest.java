package com.proyecto.test.touristplaceManagment.service;

import com.proyecto.test.touristPlaceManagment.domain.Activity;
import com.proyecto.test.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.test.touristPlaceManagment.service.ActivityService;
import com.proyecto.test.touristPlaceManagment.service.TouristPlaceService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private TouristPlaceService touristPlaceService;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void shouldReturnActivitiesByPlace() {

        UUID placeId = UUID.randomUUID();

        Activity activity = new Activity();
        activity.setId(1);
        activity.setDescription("Horse Riding");

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setActivities(List.of(activity));

        when(touristPlaceService.getById(placeId))
                .thenReturn(touristPlace);

        List<Activity> result =
                activityService.getActivitiesByPlace(placeId);

        assertEquals(1, result.size());
        assertEquals(
                "Horse Riding",
                result.get(0).getDescription()
        );
    }
}
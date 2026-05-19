package com.proyecto.test.touristPlaceManagment;

import com.proyecto.app.touristPlaceManagment.domain.Activity;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.touristPlaceManagment.dto.request.ActivityRequest;
import com.proyecto.app.touristPlaceManagment.dto.response.ActivityResponse;
import com.proyecto.app.touristPlaceManagment.dto.response.TouristPlaceResponse;
import com.proyecto.app.touristPlaceManagment.exception.ActivityNotFoundException;
import com.proyecto.app.touristPlaceManagment.repository.TouristPlaceRepository;
import com.proyecto.app.touristPlaceManagment.service.ActivityService;
import com.proyecto.app.touristPlaceManagment.service.TouristPlaceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityService - Pruebas Unitarias")
class ActivityServiceTest {

    @Mock
    private TouristPlaceService touristPlaceService;

    @Mock
    private TouristPlaceRepository placeRepository;

    @InjectMocks
    private ActivityService activityService;

    private TouristPlace place;
    private Activity activity1;
    private Activity activity2;

    @BeforeEach
    void setUp() {
        activity1 = new Activity();
        activity1.setId(Long.valueOf(1));
        activity1.setDescription("Senderismo");

        activity2 = new Activity();
        activity2.setId(Long.valueOf(2));
        activity2.setDescription("Ciclismo");

        place = mock(TouristPlace.class);
    }

    // ----------------------------------------------------------------
    // getActivitiesByPlace
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getActivitiesByPlace: retorna lista de actividades del lugar")
    void getActivitiesByPlace_returnsMappedActivities() {

        List<Activity> activities = new ArrayList<>(List.of(activity1, activity2));

        when(place.getActivities()).thenReturn(activities);
        when(touristPlaceService.resolveOrThrow(1L)).thenReturn(place);

        List<ActivityResponse> result =
                activityService.getActivitiesByPlace(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription())
                .isEqualTo("Senderismo");
        assertThat(result.get(1).getDescription())
                .isEqualTo("Ciclismo");
    }

    @Test
    @DisplayName("getActivitiesByPlace: retorna lista vacía si el lugar no tiene actividades")
    void getActivitiesByPlace_returnsEmptyIfNoActivities() {

        when(place.getActivities()).thenReturn(new ArrayList<>());
        when(touristPlaceService.resolveOrThrow(1L)).thenReturn(place);

        List<ActivityResponse> result =
                activityService.getActivitiesByPlace(1L);

        assertThat(result).isEmpty();
    }

    // ----------------------------------------------------------------
    // addActivity
    // ----------------------------------------------------------------

    @Test
    @DisplayName("addActivity: agrega actividad y guarda el lugar")
    void addActivity_addsActivityAndSavesPlace() {

        ActivityRequest request = new ActivityRequest();
        request.setDescription("Kayak");

        TouristPlaceResponse expectedResponse =
                mock(TouristPlaceResponse.class);

        when(touristPlaceService.resolveOrThrow(1L))
                .thenReturn(place);

        when(placeRepository.save(place))
                .thenReturn(place);

        when(touristPlaceService.getById(1L))
                .thenReturn(expectedResponse);

        TouristPlaceResponse result =
                activityService.addActivity(1L, request);

        assertThat(result).isNotNull();

        verify(place).addActivity(any(Activity.class));
        verify(placeRepository).save(place);
        verify(touristPlaceService).getById(1L);
    }

    @Test
    @DisplayName("addActivity: lanza excepción si el lugar no existe")
    void addActivity_throwsIfPlaceNotFound() {

        ActivityRequest request = new ActivityRequest();
        request.setDescription("Pesca");

        when(touristPlaceService.resolveOrThrow(1L))
                .thenThrow(new RuntimeException("Lugar no encontrado"));

        assertThatThrownBy(() ->
                activityService.addActivity(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Lugar no encontrado");
    }

    // ----------------------------------------------------------------
    // removeActivity
    // ----------------------------------------------------------------

    @Test
    @DisplayName("removeActivity: elimina actividad existente")
    void removeActivity_removesExistingActivity() {

        List<Activity> activities = new ArrayList<>(List.of(activity1, activity2));

        TouristPlaceResponse expectedResponse =
                mock(TouristPlaceResponse.class);

        when(place.getActivities()).thenReturn(activities);

        when(touristPlaceService.resolveOrThrow(1L))
                .thenReturn(place);

        when(placeRepository.save(place))
                .thenReturn(place);

        when(touristPlaceService.getById(1L))
                .thenReturn(expectedResponse);

        TouristPlaceResponse result =
                activityService.removeActivity(1L, 1);

        assertThat(result).isNotNull();

        verify(place).removeActivity(activity1);
        verify(placeRepository).save(place);
    }

    @Test
    @DisplayName("removeActivity: lanza excepción si la actividad no existe")
    void removeActivity_throwsActivityNotFound() {

        List<Activity> activities = new ArrayList<>(List.of(activity1, activity2));

        when(place.getActivities()).thenReturn(activities);

        when(touristPlaceService.resolveOrThrow(1L))
                .thenReturn(place);

        assertThatThrownBy(() ->
                activityService.removeActivity(1L, 999))
                .isInstanceOf(ActivityNotFoundException.class);
    }

    // ----------------------------------------------------------------
    // getActivityById
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getActivityById: retorna la actividad correcta")
    void getActivityById_returnsCorrectActivity() {

        List<Activity> activities = new ArrayList<>(List.of(activity1, activity2));

        when(place.getActivities()).thenReturn(activities);

        when(touristPlaceService.resolveOrThrow(1L))
                .thenReturn(place);

        ActivityResponse result =
                activityService.getActivityById(1L, 2);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getDescription())
                .isEqualTo("Ciclismo");
    }

    @Test
    @DisplayName("getActivityById: lanza excepción si la actividad no existe")
    void getActivityById_throwsIfActivityNotFound() {

        List<Activity> activities = new ArrayList<>(List.of(activity1, activity2));

        when(place.getActivities()).thenReturn(activities);

        when(touristPlaceService.resolveOrThrow(1L))
                .thenReturn(place);

        assertThatThrownBy(() ->
                activityService.getActivityById(1L, 999))
                .isInstanceOf(ActivityNotFoundException.class);
    }
}
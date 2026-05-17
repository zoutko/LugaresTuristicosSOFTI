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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private UUID placeId;
    private TouristPlace place;
    private Activity activity1;
    private Activity activity2;

    @BeforeEach
    void setUp() {
        placeId = UUID.randomUUID();

        activity1 = mock(Activity.class);
        when(activity1.getId()).thenReturn(1);
        when(activity1.getDescription()).thenReturn("Senderismo");

        activity2 = mock(Activity.class);
        when(activity2.getId()).thenReturn(2);
        when(activity2.getDescription()).thenReturn("Ciclismo");

        place = mock(TouristPlace.class);
        when(place.getActivities()).thenReturn(new ArrayList<>(List.of(activity1, activity2)));
    }

    // ----------------------------------------------------------------
    // getActivitiesByPlace
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getActivitiesByPlace: retorna lista de actividades del lugar")
    void getActivitiesByPlace_returnsMappedActivities() {
        when(touristPlaceService.resolveOrThrow(placeId)).thenReturn(place);

        List<ActivityResponse> result = activityService.getActivitiesByPlace(placeId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Senderismo");
        assertThat(result.get(1).getDescription()).isEqualTo("Ciclismo");
    }

    @Test
    @DisplayName("getActivitiesByPlace: retorna lista vacía si el lugar no tiene actividades")
    void getActivitiesByPlace_returnsEmptyIfNoActivities() {
        when(place.getActivities()).thenReturn(new ArrayList<>());
        when(touristPlaceService.resolveOrThrow(placeId)).thenReturn(place);

        List<ActivityResponse> result = activityService.getActivitiesByPlace(placeId);

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

        TouristPlaceResponse expectedResponse = mock(TouristPlaceResponse.class);

        when(touristPlaceService.resolveOrThrow(placeId)).thenReturn(place);
        when(placeRepository.save(place)).thenReturn(place);
        when(touristPlaceService.getById(placeId)).thenReturn(expectedResponse);

        TouristPlaceResponse result = activityService.addActivity(placeId, request);

        assertThat(result).isNotNull();
        verify(place).addActivity(any(Activity.class));
        verify(placeRepository).save(place);
        verify(touristPlaceService).getById(placeId);
    }

    @Test
    @DisplayName("addActivity: lanza excepción si el lugar no existe")
    void addActivity_throwsIfPlaceNotFound() {
        ActivityRequest request = new ActivityRequest();
        request.setDescription("Pesca");

        when(touristPlaceService.resolveOrThrow(placeId))
                .thenThrow(new RuntimeException("Lugar no encontrado"));

        assertThatThrownBy(() -> activityService.addActivity(placeId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Lugar no encontrado");
    }

    // ----------------------------------------------------------------
    // removeActivity
    // ----------------------------------------------------------------

    @Test
    @DisplayName("removeActivity: elimina actividad existente")
    void removeActivity_removesExistingActivity() {
        TouristPlaceResponse expectedResponse = mock(TouristPlaceResponse.class);

        when(touristPlaceService.resolveOrThrow(placeId)).thenReturn(place);
        when(placeRepository.save(place)).thenReturn(place);
        when(touristPlaceService.getById(placeId)).thenReturn(expectedResponse);

        TouristPlaceResponse result = activityService.removeActivity(placeId, 1);

        assertThat(result).isNotNull();
        verify(place).removeActivity(activity1);
        verify(placeRepository).save(place);
    }

    @Test
    @DisplayName("removeActivity: lanza excepción si la actividad no existe")
    void removeActivity_throwsActivityNotFound() {
        when(touristPlaceService.resolveOrThrow(placeId)).thenReturn(place);

        assertThatThrownBy(() -> activityService.removeActivity(placeId, 999))
                .isInstanceOf(ActivityNotFoundException.class);
    }

    // ----------------------------------------------------------------
    // getActivityById
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getActivityById: retorna la actividad correcta")
    void getActivityById_returnsCorrectActivity() {
        when(touristPlaceService.resolveOrThrow(placeId)).thenReturn(place);

        ActivityResponse result = activityService.getActivityById(placeId, 2);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getDescription()).isEqualTo("Ciclismo");
    }

    @Test
    @DisplayName("getActivityById: lanza excepción si la actividad no existe")
    void getActivityById_throwsIfActivityNotFound() {
        when(touristPlaceService.resolveOrThrow(placeId)).thenReturn(place);

        assertThatThrownBy(() -> activityService.getActivityById(placeId, 999))
                .isInstanceOf(ActivityNotFoundException.class);
    }
}

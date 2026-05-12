package com.proyecto.test.touristplaceManagment.service;

import com.proyecto.test.common.Category;
import com.proyecto.test.common.Environment;
import com.proyecto.test.touristPlaceManagment.domain.Activity;
import com.proyecto.test.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.test.touristPlaceManagment.service.TouristPlaceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TouristPlaceServiceTest {

    private TouristPlaceService touristPlaceService;

    @BeforeEach
    void setUp() {
        touristPlaceService = new TouristPlaceService();
    }

    @Test
    void shouldCreateTouristPlace() {

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setName("Villa de Leyva");

        TouristPlace result =
                touristPlaceService.create(touristPlace);

        assertNotNull(result.getId());
        assertEquals(
                "Villa de Leyva",
                result.getName()
        );

        assertEquals(
                1,
                touristPlaceService.getAll().size()
        );
    }

    @Test
    void shouldReturnTouristPlaceById() {

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setName("Mongui");

        TouristPlace createdPlace =
                touristPlaceService.create(touristPlace);

        TouristPlace result =
                touristPlaceService.getById(
                        createdPlace.getId()
                );

        assertEquals(
                createdPlace.getId(),
                result.getId()
        );
    }

    @Test
    void shouldThrowExceptionWhenPlaceDoesNotExist() {

        UUID id = UUID.randomUUID();

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> touristPlaceService.getById(id)
                );

        assertEquals(
                "Place not found: " + id,
                exception.getMessage()
        );
    }

    @Test
    void shouldUpdateTouristPlace() {

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setName("Old Name");

        TouristPlace createdPlace =
                touristPlaceService.create(touristPlace);

        TouristPlace updatedPlace = new TouristPlace();
        updatedPlace.setName("New Name");
        updatedPlace.setDescription("Updated Description");

        TouristPlace result =
                touristPlaceService.update(
                        createdPlace.getId(),
                        updatedPlace
                );

        assertEquals(
                "New Name",
                result.getName()
        );

        assertEquals(
                "Updated Description",
                result.getDescription()
        );
    }

    @Test
    void shouldDeleteTouristPlace() {

        TouristPlace touristPlace = new TouristPlace();

        TouristPlace createdPlace =
                touristPlaceService.create(touristPlace);

        touristPlaceService.delete(createdPlace.getId());

        assertEquals(
                0,
                touristPlaceService.getAll().size()
        );
    }

    @Test
    void shouldReturnPlacesByName() {

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setName("Villa de Leyva");

        touristPlaceService.create(touristPlace);

        List<TouristPlace> result =
                touristPlaceService.getByName("villa");

        assertEquals(1, result.size());
    }

    @Test
    void shouldAddActivityToTouristPlace() {

        TouristPlace touristPlace = new TouristPlace();

        TouristPlace createdPlace =
                touristPlaceService.create(touristPlace);

        Activity activity = new Activity();
        activity.setId(1);
        activity.setDescription("Horse Riding");
        TouristPlace result =
                touristPlaceService.addActivity(
                        createdPlace.getId(),
                        activity
                );

        assertEquals(
                1,
                result.getActivities().size()
        );
    }

    @Test
    void shouldRemoveActivityFromTouristPlace() {

        TouristPlace touristPlace = new TouristPlace();

        TouristPlace createdPlace =
                touristPlaceService.create(touristPlace);

        Activity activity = new Activity();
        activity.setId(1);

        touristPlaceService.addActivity(
                createdPlace.getId(),
                activity
        );

        TouristPlace result =
                touristPlaceService.removeActivity(
                        createdPlace.getId(),
                        activity
                );

        assertEquals(
                0,
                result.getActivities().size()
        );
    }

    @Test
    void shouldAddCategoryToTouristPlace() {

        TouristPlace touristPlace = new TouristPlace();

        TouristPlace createdPlace =
                touristPlaceService.create(touristPlace);

        Category category = new Category();
        category.setId(1L);

        TouristPlace result =
                touristPlaceService.addCategory(
                        createdPlace.getId(),
                        category
                );

        assertEquals(
                1,
                result.getCategories().size()
        );
    }

    @Test
    void shouldReturnPlacesByEnvironment() {

        TouristPlace touristPlace = new TouristPlace();
        touristPlace.setEnvironment(Environment.EXTERIOR);

        touristPlaceService.create(touristPlace);

        List<TouristPlace> result =
                touristPlaceService.getByEnvironment(
                        Environment.EXTERIOR
                );

        assertEquals(1, result.size());
    }
}
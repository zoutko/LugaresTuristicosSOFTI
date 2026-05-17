package com.proyecto.test.touristPlaceManagment;

import com.proyecto.app.catalog.domain.Category;
import com.proyecto.app.catalog.repository.CategoryRepository;
import com.proyecto.app.common.Environment;
import com.proyecto.app.media.domain.Album;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import com.proyecto.app.touristPlaceManagment.dto.request.TouristPlaceRequest;
import com.proyecto.app.touristPlaceManagment.dto.response.TouristPlaceResponse;
import com.proyecto.app.touristPlaceManagment.exception.InvalidPlaceDataException;
import com.proyecto.app.touristPlaceManagment.exception.TouristPlaceNotFoundException;
import com.proyecto.app.touristPlaceManagment.repository.TouristPlaceRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TouristPlaceService - Pruebas Unitarias")
class TouristPlaceServiceTest {

    @Mock
    private TouristPlaceRepository placeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TouristPlaceService touristPlaceService;

    private UUID placeId;
    private TouristPlace place;

    @BeforeEach
    void setUp() {
        placeId = UUID.randomUUID();

        Album album = new Album();
        album.setName("Test Album");

        place = mock(TouristPlace.class);
        when(place.getId()).thenReturn(placeId);
        when(place.getName()).thenReturn("Cerro Quitasol");
        when(place.getDescription()).thenReturn("Hermoso cerro");
        when(place.getEnvironment()).thenReturn(Environment.EXTERIOR);
        when(place.getActivities()).thenReturn(List.of());
        when(place.getCategories()).thenReturn(new ArrayList<>());
        when(place.getAlbum()).thenReturn(album);
    }

    @Test
    @DisplayName("getAll: retorna lista de todos los lugares")
    void getAll_returnsAllPlaces() {
        when(placeRepository.findAll()).thenReturn(List.of(place));

        List<TouristPlaceResponse> result = touristPlaceService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cerro Quitasol");
    }

    @Test
    @DisplayName("getAll: retorna lista vacía si no hay lugares")
    void getAll_returnsEmptyList() {
        when(placeRepository.findAll()).thenReturn(List.of());

        List<TouristPlaceResponse> result = touristPlaceService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getById: retorna el lugar si existe")
    void getById_returnsPlaceIfFound() {
        when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));

        TouristPlaceResponse result = touristPlaceService.getById(placeId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Cerro Quitasol");
    }

    @Test
    @DisplayName("getById: lanza excepción si el lugar no existe")
    void getById_throwsIfNotFound() {
        when(placeRepository.findById(placeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> touristPlaceService.getById(placeId))
                .isInstanceOf(TouristPlaceNotFoundException.class);
    }

    @Test
    @DisplayName("getByName: retorna lugares que coinciden con el nombre")
    void getByName_returnsMatchingPlaces() {
        when(placeRepository.findByNameContainingIgnoreCase("cerro")).thenReturn(List.of(place));

        List<TouristPlaceResponse> result = touristPlaceService.getByName("cerro");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cerro Quitasol");
    }

    @Test
    @DisplayName("getByName: retorna vacío si no hay coincidencias")
    void getByName_returnsEmptyIfNoMatch() {
        when(placeRepository.findByNameContainingIgnoreCase("xyz")).thenReturn(List.of());

        List<TouristPlaceResponse> result = touristPlaceService.getByName("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getByCity: retorna lugares de la ciudad indicada")
    void getByCity_returnsPlacesInCity() {
        when(placeRepository.findByLocationCityIgnoreCase("Tunja")).thenReturn(List.of(place));

        List<TouristPlaceResponse> result = touristPlaceService.getByCity("Tunja");

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getByEnvironment: retorna lugares con el ambiente indicado")
    void getByEnvironment_returnsMatchingPlaces() {
        when(placeRepository.findByEnvironment(Environment.EXTERIOR)).thenReturn(List.of(place));

        List<TouristPlaceResponse> result = touristPlaceService.getByEnvironment(Environment.EXTERIOR);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEnvironment()).isEqualTo(Environment.EXTERIOR);
    }

    @Test
    @DisplayName("create: lanza excepción si el nombre está vacío")
    void create_throwsIfNameIsBlank() {
        TouristPlaceRequest request = new TouristPlaceRequest();
        request.setName("  ");

        assertThatThrownBy(() -> touristPlaceService.create(request))
                .isInstanceOf(InvalidPlaceDataException.class)
                .hasMessageContaining("nombre del lugar no puede estar vacío");
    }

    @Test
    @DisplayName("create: lanza excepción si el nombre es null")
    void create_throwsIfNameIsNull() {
        TouristPlaceRequest request = new TouristPlaceRequest();
        request.setName(null);

        assertThatThrownBy(() -> touristPlaceService.create(request))
                .isInstanceOf(InvalidPlaceDataException.class);
    }

    @Test
    @DisplayName("create: crea y guarda el lugar correctamente")
    void create_savesPlaceSuccessfully() {
        TouristPlaceRequest request = new TouristPlaceRequest();
        request.setName("Lago de Tota");
        request.setDescription("Lago hermoso");
        request.setEnvironment(Environment.EXTERIOR);
        request.setCategoryIds(List.of());

        TouristPlace savedPlace = mock(TouristPlace.class);
        when(savedPlace.getId()).thenReturn(UUID.randomUUID());
        when(savedPlace.getName()).thenReturn("Lago de Tota");
        when(savedPlace.getEnvironment()).thenReturn(Environment.EXTERIOR);
        when(savedPlace.getActivities()).thenReturn(List.of());
        when(savedPlace.getCategories()).thenReturn(new ArrayList<>());
        when(savedPlace.getAlbum()).thenReturn(null);

        when(placeRepository.save(any(TouristPlace.class))).thenReturn(savedPlace);

        TouristPlaceResponse result = touristPlaceService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Lago de Tota");
        verify(placeRepository).save(any(TouristPlace.class));
    }

    @Test
    @DisplayName("create: asigna categorías correctamente cuando se proveen")
    void create_assignsCategoriesWhenProvided() {
        UUID categoryId = UUID.randomUUID();
        Category category = mock(Category.class);
        when(category.getName()).thenReturn("Naturaleza");

        TouristPlaceRequest request = new TouristPlaceRequest();
        request.setName("Cascada");
        request.setCategoryIds(List.of(categoryId));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        TouristPlace savedPlace = mock(TouristPlace.class);
        when(savedPlace.getId()).thenReturn(UUID.randomUUID());
        when(savedPlace.getName()).thenReturn("Cascada");
        when(savedPlace.getActivities()).thenReturn(List.of());
        when(savedPlace.getCategories()).thenReturn(List.of(category));
        when(savedPlace.getAlbum()).thenReturn(null);

        when(placeRepository.save(any(TouristPlace.class))).thenReturn(savedPlace);

        TouristPlaceResponse result = touristPlaceService.create(request);

        assertThat(result.getCategories()).contains("Naturaleza");
    }

    @Test
    @DisplayName("create: lanza excepción si una categoría no existe")
    void create_throwsIfCategoryNotFound() {
        UUID badCategoryId = UUID.randomUUID();

        TouristPlaceRequest request = new TouristPlaceRequest();
        request.setName("Lugar X");
        request.setCategoryIds(List.of(badCategoryId));

        when(categoryRepository.findById(badCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> touristPlaceService.create(request))
                .isInstanceOf(InvalidPlaceDataException.class)
                .hasMessageContaining("Categoría no encontrada");
    }

    @Test
    @DisplayName("update: lanza excepción si el lugar no existe")
    void update_throwsIfPlaceNotFound() {
        TouristPlaceRequest request = new TouristPlaceRequest();
        request.setName("Lugar Actualizado");

        when(placeRepository.findById(placeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> touristPlaceService.update(placeId, request))
                .isInstanceOf(TouristPlaceNotFoundException.class);
    }

    @Test
    @DisplayName("update: actualiza y retorna el lugar")
    void update_updatesAndReturnsPlace() {
        TouristPlaceRequest request = new TouristPlaceRequest();
        request.setName("Quitasol Actualizado");
        request.setCategoryIds(List.of());

        // place es un objeto real para que applyUpdate pueda setearlo
        TouristPlace realPlace = new TouristPlace();
        realPlace.setName("Original");

        Album album = new Album();
        realPlace.setAlbum(album);

        when(placeRepository.findById(placeId)).thenReturn(Optional.of(realPlace));
        when(placeRepository.save(any(TouristPlace.class))).thenAnswer(inv -> inv.getArgument(0));

        TouristPlaceResponse result = touristPlaceService.update(placeId, request);

        assertThat(result.getName()).isEqualTo("Quitasol Actualizado");
    }


    @Test
    @DisplayName("delete: elimina el lugar si existe")
    void delete_deletesPlaceIfFound() {
        when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));

        touristPlaceService.delete(placeId);

        verify(placeRepository).delete(place);
    }

    @Test
    @DisplayName("delete: lanza excepción si el lugar no existe")
    void delete_throwsIfNotFound() {
        when(placeRepository.findById(placeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> touristPlaceService.delete(placeId))
                .isInstanceOf(TouristPlaceNotFoundException.class);
    }

    // ----------------------------------------------------------------
    // resolveOrThrow
    // ----------------------------------------------------------------

    @Test
    @DisplayName("resolveOrThrow: retorna el lugar si existe")
    void resolveOrThrow_returnsPlace() {
        when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));

        TouristPlace result = touristPlaceService.resolveOrThrow(placeId);

        assertThat(result).isEqualTo(place);
    }

    @Test
    @DisplayName("resolveOrThrow: lanza TouristPlaceNotFoundException si no existe")
    void resolveOrThrow_throwsIfNotFound() {
        when(placeRepository.findById(placeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> touristPlaceService.resolveOrThrow(placeId))
                .isInstanceOf(TouristPlaceNotFoundException.class);
    }
}

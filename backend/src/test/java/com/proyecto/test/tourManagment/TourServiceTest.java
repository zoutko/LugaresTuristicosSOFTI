package com.proyecto.test.tourManagment;

import com.proyecto.app.catalog.domain.Category;
import com.proyecto.app.catalog.repository.CategoryRepository;
import com.proyecto.app.common.Environment;
import com.proyecto.app.common.Location;
import com.proyecto.app.media.domain.Album;
import com.proyecto.app.media.service.AlbumService;
import com.proyecto.app.touristPlaceManagment.api.PlaceQueryService;
import com.proyecto.app.tourManagment.domain.*;
import com.proyecto.app.tourManagment.dto.request.CreateTourRequest;
import com.proyecto.app.tourManagment.dto.request.DiscountRequest;
import com.proyecto.app.tourManagment.dto.request.TourFilterRequest;
import com.proyecto.app.tourManagment.dto.request.UpdateTourRequest;
import com.proyecto.app.tourManagment.dto.response.TourOfferResponse;
import com.proyecto.app.tourManagment.dto.response.TourResponse;
import com.proyecto.app.tourManagment.exception.*;
import com.proyecto.app.tourManagment.repository.*;
import com.proyecto.app.tourManagment.service.TourEnvironmentValidator;
import com.proyecto.app.tourManagment.service.TourService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock private TourRepository tourRepository;
    @Mock private TourOfferRepository tourOfferRepository;
    @Mock private ItineraryRepository itineraryRepository;
    @Mock private UserTypeRepository userTypeRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AlbumService albumService;                      // ← agregado
    @Mock private PlaceQueryService placeQueryService;            // ← reemplaza TouristPlaceRepository
    @Mock private TourEnvironmentValidator environmentValidator;  // ← agregado

    @InjectMocks
    private TourService tourService;

    private Tour tour;
    private TourOffer tourOffer;
    private UserType userType;
    private Category category;
    private Location location;
    private Album album;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setCity("Bogotá");
        location.setDepartment("Cundinamarca");
        location.setCountry("Colombia");
        location.setLatitude(4.7110);

        category = new Category();
        category.setId(1L);
        category.setName("Aventura");

        userType = new UserType();
        userType.setId(1L);
        userType.setName("STUDENT");

        album = new Album();

        tour = new Tour();
        tour.setId(1L);
        tour.setName("Tour Bogotá");
        tour.setDescription("Recorrido por Bogotá");
        tour.setRecommendations("Llevar ropa abrigada");
        tour.setPrice(150000);
        tour.setEnvironment(Environment.EXTERIOR);
        tour.setLocation(location);
        tour.setMeetingPoint(location);
        tour.setCategories(new ArrayList<>(List.of(category)));
        tour.setItinerary(new ArrayList<>());
        tour.setAlbum(album);

        tourOffer = new TourOffer(tour, 120000);
        tourOffer.setId(1L);
        tourOffer.setDiscounts(new ArrayList<>());
        tour.setTourOffer(tourOffer);
    }

    // ── createTour ─────────────────────────────────

    @Test
    void createTour_exitoso() {
        CreateTourRequest request = buildCreateRequest();

        when(categoryRepository.findAllById(any())).thenReturn(List.of(category));
        when(albumService.findOrCreate(any())).thenReturn(album);
        when(placeQueryService.exists(1L)).thenReturn(true);
        when(tourRepository.save(any())).thenReturn(tour);
        when(itineraryRepository.saveAll(any())).thenReturn(List.of());
        when(tourOfferRepository.save(any())).thenReturn(tourOffer);
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourResponse response = tourService.createTour(request);

        assertNotNull(response);
        assertEquals("Tour Bogotá", response.getName());
        verify(tourRepository, times(1)).save(any());
        verify(tourOfferRepository, times(1)).save(any());
    }

    @Test
    void createTour_nombreNulo_lanzaInvalidTourDataException() {
        CreateTourRequest request = buildCreateRequest();
        request.setName(null);

        assertThrows(InvalidTourDataException.class, () -> tourService.createTour(request));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void createTour_nombreBlanco_lanzaInvalidTourDataException() {
        CreateTourRequest request = buildCreateRequest();
        request.setName("   ");

        assertThrows(InvalidTourDataException.class, () -> tourService.createTour(request));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void createTour_precioNegativo_lanzaInvalidTourDataException() {
        CreateTourRequest request = buildCreateRequest();
        request.setBasePrice(-1000);

        assertThrows(InvalidTourDataException.class, () -> tourService.createTour(request));
        verify(tourRepository, never()).save(any());
    }

    @Test
    void createTour_environmentInvalido_lanzaInvalidTourDataException() {
        CreateTourRequest request = buildCreateRequest();
        request.setEnvironment("INVALIDO");

        assertThrows(Exception.class, () -> tourService.createTour(request));
    }

    @Test
    void createTour_placeNoExiste_lanzaInvalidTourDataException() {
        CreateTourRequest request = buildCreateRequest();

        when(categoryRepository.findAllById(any())).thenReturn(List.of(category));
        when(albumService.findOrCreate(any())).thenReturn(album);
        when(tourRepository.save(any())).thenReturn(tour);
        when(placeQueryService.exists(1L)).thenReturn(false);

        assertThrows(InvalidTourDataException.class, () -> tourService.createTour(request));
    }

    // ── getTourById ────────────────────────────────

    @Test
    void getTourById_exitoso() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourResponse response = tourService.getTourById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tour Bogotá", response.getName());
    }

    @Test
    void getTourById_noExiste_lanzaTourNotFoundException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TourNotFoundException.class, () -> tourService.getTourById(99L));
    }

    // ── getAllTours ────────────────────────────────

    @Test
    void getAllTours_retornaLista() {
        when(tourRepository.findAll()).thenReturn(List.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        List<TourResponse> result = tourService.getAllTours();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllTours_listaVacia() {
        when(tourRepository.findAll()).thenReturn(List.of());

        List<TourResponse> result = tourService.getAllTours();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── updateTour ─────────────────────────────────

    @Test
    void updateTour_exitoso() {
        UpdateTourRequest request = new UpdateTourRequest();
        request.setName("Tour Bogotá Actualizado");
        request.setPrice(200000.0);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourRepository.save(any())).thenReturn(tour);
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourResponse response = tourService.updateTour(1L, request);

        assertNotNull(response);
        verify(tourRepository).save(any());
    }

    @Test
    void updateTour_tourNoExiste_lanzaTourNotFoundException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TourNotFoundException.class,
                () -> tourService.updateTour(99L, new UpdateTourRequest()));
    }

    @Test
    void updateTour_nombreBlanco_lanzaInvalidTourDataException() {
        UpdateTourRequest request = new UpdateTourRequest();
        request.setName("   ");

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        assertThrows(InvalidTourDataException.class, () -> tourService.updateTour(1L, request));
    }

    @Test
    void updateTour_precioNegativo_lanzaInvalidTourDataException() {
        UpdateTourRequest request = new UpdateTourRequest();
        request.setPrice(-500.0);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        assertThrows(InvalidTourDataException.class, () -> tourService.updateTour(1L, request));
    }

    @Test
    void updateTour_environmentInvalido_lanzaInvalidTourDataException() {
        UpdateTourRequest request = new UpdateTourRequest();
        request.setEnvironment("MAL_VALOR");

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        assertThrows(InvalidTourDataException.class, () -> tourService.updateTour(1L, request));
    }

    // ── deleteTour ─────────────────────────────────

    @Test
    void deleteTour_exitoso() {
        when(tourRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tourRepository).deleteById(1L);

        assertDoesNotThrow(() -> tourService.deleteTour(1L));
        verify(tourRepository).deleteById(1L);
    }

    @Test
    void deleteTour_noExiste_lanzaTourNotFoundException() {
        when(tourRepository.existsById(99L)).thenReturn(false);

        assertThrows(TourNotFoundException.class, () -> tourService.deleteTour(99L));
        verify(tourRepository, never()).deleteById(any());
    }

    // ── addPlaceToItinerary ────────────────────────

    @Test
    void addPlaceToItinerary_exitoso() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(placeQueryService.exists(1L)).thenReturn(true);
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(new ArrayList<>());
        when(itineraryRepository.save(any())).thenReturn(new Itinerary(tour, 1L, 1));

        TourResponse response = tourService.addPlaceToItinerary(1L, 1L);

        assertNotNull(response);
        verify(itineraryRepository).save(any());
    }

    @Test
    void addPlaceToItinerary_tourNoExiste_lanzaTourNotFoundException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TourNotFoundException.class,
                () -> tourService.addPlaceToItinerary(99L, 1L));
    }

    @Test
    void addPlaceToItinerary_placeNoExiste_lanzaInvalidTourDataException() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(placeQueryService.exists(99L)).thenReturn(false);

        assertThrows(InvalidTourDataException.class,
                () -> tourService.addPlaceToItinerary(1L, 99L));
    }

    @Test
    void addPlaceToItinerary_placeDuplicado_lanzaInvalidTourDataException() {
        Itinerary existingItem = new Itinerary(tour, 1L, 1);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(placeQueryService.exists(1L)).thenReturn(true);
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L))
                .thenReturn(new ArrayList<>(List.of(existingItem)));

        assertThrows(InvalidTourDataException.class,
                () -> tourService.addPlaceToItinerary(1L, 1L));
    }

    // ── removePlaceFromItinerary ───────────────────

    @Test
    void removePlaceFromItinerary_exitoso() {
        when(tourRepository.existsById(1L)).thenReturn(true);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        doNothing().when(itineraryRepository).deleteByTourIdAndTouristPlaceId(1L, 1L);
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(new ArrayList<>());

        TourResponse response = tourService.removePlaceFromItinerary(1L, 1L);

        assertNotNull(response);
        verify(itineraryRepository).deleteByTourIdAndTouristPlaceId(1L, 1L);
    }

    @Test
    void removePlaceFromItinerary_tourNoExiste_lanzaTourNotFoundException() {
        when(tourRepository.existsById(99L)).thenReturn(false);

        assertThrows(TourNotFoundException.class,
                () -> tourService.removePlaceFromItinerary(99L, 1L));
    }

    // ── addDiscount ────────────────────────────────

    @Test
    void addDiscount_exitoso() {
        DiscountRequest request = new DiscountRequest();
        request.setUserTypeId(1L);
        request.setPercentage(15);

        when(tourOfferRepository.findByTourId(1L)).thenReturn(Optional.of(tourOffer));
        when(userTypeRepository.findById(1L)).thenReturn(Optional.of(userType));
        when(tourOfferRepository.save(any())).thenReturn(tourOffer);

        TourOfferResponse response = tourService.addDiscount(1L, request);

        assertNotNull(response);
        verify(tourOfferRepository).save(any());
    }

    @Test
    void addDiscount_porcentajeCero_lanzaInvalidTourDataException() {
        DiscountRequest request = new DiscountRequest();
        request.setUserTypeId(1L);
        request.setPercentage(0);

        assertThrows(InvalidTourDataException.class, () -> tourService.addDiscount(1L, request));
    }

    @Test
    void addDiscount_porcentajeMayorCien_lanzaInvalidTourDataException() {
        DiscountRequest request = new DiscountRequest();
        request.setUserTypeId(1L);
        request.setPercentage(101);

        assertThrows(InvalidTourDataException.class, () -> tourService.addDiscount(1L, request));
    }

    @Test
    void addDiscount_offerNoExiste_lanzaTourOfferNotFoundException() {
        DiscountRequest request = new DiscountRequest();
        request.setUserTypeId(1L);
        request.setPercentage(10);

        when(tourOfferRepository.findByTourId(99L)).thenReturn(Optional.empty());

        assertThrows(TourOfferNotFoundException.class, () -> tourService.addDiscount(99L, request));
    }

    @Test
    void addDiscount_userTypeNoExiste_lanzaUserTypeNotFoundException() {
        DiscountRequest request = new DiscountRequest();
        request.setUserTypeId(99L);
        request.setPercentage(10);

        when(tourOfferRepository.findByTourId(1L)).thenReturn(Optional.of(tourOffer));
        when(userTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserTypeNotFoundException.class, () -> tourService.addDiscount(1L, request));
    }

    @Test
    void addDiscount_userTypeDuplicado_lanzaInvalidTourDataException() {
        Discount existing = new Discount();
        existing.setUserType(userType);
        existing.setPercentage(10);
        tourOffer.getDiscounts().add(existing);

        DiscountRequest request = new DiscountRequest();
        request.setUserTypeId(1L);
        request.setPercentage(20);

        when(tourOfferRepository.findByTourId(1L)).thenReturn(Optional.of(tourOffer));
        when(userTypeRepository.findById(1L)).thenReturn(Optional.of(userType));

        assertThrows(InvalidTourDataException.class, () -> tourService.addDiscount(1L, request));
    }

    // ── removeDiscount ─────────────────────────────

    @Test
    void removeDiscount_exitoso() {
        Discount discount = new Discount();
        discount.setId(10L);
        discount.setUserType(userType);
        discount.setPercentage(15);
        tourOffer.getDiscounts().add(discount);

        when(tourOfferRepository.findByTourId(1L)).thenReturn(Optional.of(tourOffer));
        when(tourOfferRepository.save(any())).thenReturn(tourOffer);

        TourOfferResponse response = tourService.removeDiscount(1L, 10L);

        assertNotNull(response);
        verify(tourOfferRepository).save(any());
    }

    @Test
    void removeDiscount_offerNoExiste_lanzaTourOfferNotFoundException() {
        when(tourOfferRepository.findByTourId(99L)).thenReturn(Optional.empty());

        assertThrows(TourOfferNotFoundException.class,
                () -> tourService.removeDiscount(99L, 1L));
    }

    @Test
    void removeDiscount_discountNoExiste_lanzaInvalidTourDataException() {
        when(tourOfferRepository.findByTourId(1L)).thenReturn(Optional.of(tourOffer));

        assertThrows(InvalidTourDataException.class,
                () -> tourService.removeDiscount(1L, 999L));
    }

    // ── filterTours ────────────────────────────────

    @Test
    void filterTours_sinFiltros_retornaTodos() {
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourFilterRequest filters = new TourFilterRequest();
        List<TourResponse> result = tourService.filterTours(filters);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterTours_conNombre_retornaCoincidencias() {
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourFilterRequest filters = new TourFilterRequest();
        filters.setName("Bogotá");

        List<TourResponse> result = tourService.filterTours(filters);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tour Bogotá", result.get(0).getName());
    }

    @Test
    void filterTours_sinCoincidencias_retornaListaVacia() {
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of());

        TourFilterRequest filters = new TourFilterRequest();
        filters.setName("xyz_no_existe");

        List<TourResponse> result = tourService.filterTours(filters);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterTours_conEnvironment_retornaFiltrados() {
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourFilterRequest filters = new TourFilterRequest();
        filters.setEnvironments(List.of("EXTERIOR"));

        List<TourResponse> result = tourService.filterTours(filters);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterTours_conMaxPrice_retornaFiltrados() {
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourFilterRequest filters = new TourFilterRequest();
        filters.setMaxPrice(200000.0);

        List<TourResponse> result = tourService.filterTours(filters);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterTours_conCategorias_retornaFiltrados() {
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourFilterRequest filters = new TourFilterRequest();
        filters.setCategoryIds(List.of(1L));

        List<TourResponse> result = tourService.filterTours(filters);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void filterTours_filtrosCombinados_retornaFiltrados() {
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of(tour));
        when(itineraryRepository.findByTourIdOrderByPositionAsc(1L)).thenReturn(List.of());

        TourFilterRequest filters = new TourFilterRequest();
        filters.setName("Bogotá");
        filters.setEnvironments(List.of("EXTERIOR"));
        filters.setMaxPrice(200000.0);
        filters.setCategoryIds(List.of(1L));

        List<TourResponse> result = tourService.filterTours(filters);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ── Helper ─────────────────────────────────────

    private CreateTourRequest buildCreateRequest() {
        CreateTourRequest request = new CreateTourRequest();
        request.setName("Tour Bogotá");
        request.setDescription("Recorrido por Bogotá");
        request.setRecommendations("Llevar ropa abrigada");
        request.setPrice(150000);
        request.setEnvironment("EXTERIOR");
        request.setBasePrice(120000);
        request.setLocation(location);
        request.setMeetingPoint(location);
        request.setCategoryIds(List.of(1L));
        request.setItineraryPlaceIds(List.of(1L));
        return request;
    }
}
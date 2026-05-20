package com.proyecto.app.tourManagment.service;

import com.proyecto.app.catalog.domain.Category;
import com.proyecto.app.catalog.repository.CategoryRepository;
import com.proyecto.app.common.Environment;
import com.proyecto.app.media.domain.Album;
import com.proyecto.app.media.dto.response.AlbumResponse;
import com.proyecto.app.media.dto.response.PhotoResponse;
import com.proyecto.app.media.service.AlbumService;
import com.proyecto.app.touristPlaceManagment.api.PlaceQueryService;
import com.proyecto.app.tourManagment.domain.*;
import com.proyecto.app.tourManagment.dto.request.CreateTourRequest;
import com.proyecto.app.tourManagment.dto.request.DiscountRequest;
import com.proyecto.app.tourManagment.dto.request.TourFilterRequest;
import com.proyecto.app.tourManagment.dto.request.UpdateTourRequest;
import com.proyecto.app.tourManagment.dto.response.DiscountResponse;
import com.proyecto.app.tourManagment.dto.response.ItineraryItemResponse;
import com.proyecto.app.tourManagment.dto.response.TourOfferResponse;
import com.proyecto.app.tourManagment.dto.response.TourResponse;
import com.proyecto.app.tourManagment.exception.*;
import com.proyecto.app.tourManagment.repository.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final TourOfferRepository tourOfferRepository;
    private final ItineraryRepository itineraryRepository;
    private final UserTypeRepository userTypeRepository;
    private final CategoryRepository categoryRepository;
    private final AlbumService albumService;
    private final PlaceQueryService placeQueryService;
    private final TourEnvironmentValidator environmentValidator;

    public TourService(TourRepository tourRepository,
            TourOfferRepository tourOfferRepository,
            ItineraryRepository itineraryRepository,
            UserTypeRepository userTypeRepository,
            CategoryRepository categoryRepository,
            AlbumService albumService,
            PlaceQueryService placeQueryService,
            TourEnvironmentValidator environmentValidator) {
        this.tourRepository = tourRepository;
        this.tourOfferRepository = tourOfferRepository;
        this.itineraryRepository = itineraryRepository;
        this.userTypeRepository = userTypeRepository;
        this.categoryRepository = categoryRepository;
        this.albumService = albumService;
        this.placeQueryService = placeQueryService;
        this.environmentValidator = environmentValidator;
    }

    // ── TOUR CRUD ──────────────────────────────────

    @Transactional
    public TourResponse createTour(CreateTourRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidTourDataException("El nombre del tour es obligatorio");
        }
        if (request.getBasePrice() < 0) {
            throw new InvalidTourDataException("El precio base no puede ser negativo");
        }

        Tour tour = new Tour();
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setRecommendations(request.getRecommendations());
        tour.setPrice(request.getPrice());

        if (request.getEnvironment() != null && request.getItineraryPlaceIds() != null) {
            environmentValidator.validateConsistency(
                    Environment.valueOf(request.getEnvironment()),
                    request.getItineraryPlaceIds());
        }
        if (request.getEnvironment() == null && request.getItineraryPlaceIds() != null) {
            tour.setEnvironment(environmentValidator.inferEnvironment(request.getItineraryPlaceIds()));
        }

        if (request.getLocation() != null)
            tour.setLocation(request.getLocation());
        if (request.getMeetingPoint() != null)
            tour.setMeetingPoint(request.getMeetingPoint());

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            tour.setCategories(categories);
        }


        Album album = albumService.findOrCreate("tour-" + request.getName());
        tour.setAlbum(album);

        Tour saved = tourRepository.save(tour);


        if (request.getItineraryPlaceIds() != null && !request.getItineraryPlaceIds().isEmpty()) {
            List<Itinerary> items = new ArrayList<>();
            for (int i = 0; i < request.getItineraryPlaceIds().size(); i++) {
                Long placeId = request.getItineraryPlaceIds().get(i);
                if (!placeQueryService.exists(placeId)) {
                    throw new InvalidTourDataException("TouristPlace no encontrado con id: " + placeId);
                }
                items.add(new Itinerary(saved, placeId, i + 1));
            }
            itineraryRepository.saveAll(items);
            saved.setItinerary(items);
        }

        // TourOffer
        TourOffer offer = new TourOffer(saved, request.getBasePrice());
        tourOfferRepository.save(offer);
        saved.setTourOffer(offer);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TourResponse getTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new TourNotFoundException(id));
        return toResponse(tour);
    }

    @Transactional(readOnly = true)
    public List<TourResponse> getAllTours() {
        return tourRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TourResponse updateTour(Long id, UpdateTourRequest request) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new TourNotFoundException(id));

        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new InvalidTourDataException("El nombre del tour no puede estar vacío");
            }
            tour.setName(request.getName());
        }
        if (request.getDescription() != null)
            tour.setDescription(request.getDescription());
        if (request.getRecommendations() != null)
            tour.setRecommendations(request.getRecommendations());
        if (request.getPrice() != null) {
            if (request.getPrice() < 0) {
                throw new InvalidTourDataException("El precio no puede ser negativo");
            }
            tour.setPrice(request.getPrice());
        }
        if (request.getEnvironment() != null) {
            try {
                Environment env = Environment.valueOf(request.getEnvironment());

                if (request.getItineraryPlaceIds() != null && !request.getItineraryPlaceIds().isEmpty()) {
                    environmentValidator.validateConsistency(env, request.getItineraryPlaceIds());
                } else {
                    List<Long> currentPlaceIds = tour.getItinerary().stream()
                            .map(Itinerary::getTouristPlaceId)
                            .collect(Collectors.toList());
                    environmentValidator.validateConsistency(env, currentPlaceIds);
                }

                tour.setEnvironment(env);
            } catch (IllegalArgumentException e) {
                throw new InvalidTourDataException(
                        "Environment inválido: " + request.getEnvironment() +
                                ". Valores válidos: INTERIOR, MIXED, EXTERIOR");
            }
        }

        if (request.getEnvironment() == null && request.getItineraryPlaceIds() != null
                && !request.getItineraryPlaceIds().isEmpty()) {
            tour.setEnvironment(environmentValidator.inferEnvironment(request.getItineraryPlaceIds()));
        }

        if (request.getLocation() != null)
            tour.setLocation(request.getLocation());
        if (request.getMeetingPoint() != null)
            tour.setMeetingPoint(request.getMeetingPoint());

        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            tour.setCategories(categories);
        }

        return toResponse(tourRepository.save(tour));
    }

    @Transactional
    public void deleteTour(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new TourNotFoundException(id);
        }
        tourRepository.deleteById(id);
    }

    // ── ITINERARY ──────────────────────────────────

    @Transactional
    public TourResponse addPlaceToItinerary(Long tourId, Long placeId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException(tourId));
        if (!placeQueryService.exists(placeId)) {
            throw new InvalidTourDataException("TouristPlace no encontrado con id: " + placeId);
        }
        boolean alreadyExists = itineraryRepository
                .findByTourIdOrderByPositionAsc(tourId).stream()
                .anyMatch(i -> i.getTouristPlaceId().equals(placeId));
        if (alreadyExists) {
            throw new InvalidTourDataException(
                    "El lugar con id " + placeId + " ya existe en el itinerario del tour");
        }
        int nextPosition = itineraryRepository.findByTourIdOrderByPositionAsc(tourId).size() + 1;
        itineraryRepository.save(new Itinerary(tour, placeId, nextPosition));
        return toResponse(tourRepository.findById(tourId)
        .orElseThrow(() -> new TourNotFoundException(tourId)));
    }

    @Transactional
    public TourResponse removePlaceFromItinerary(Long tourId, Long placeId) {
        if (!tourRepository.existsById(tourId)) {
            throw new TourNotFoundException(tourId);
        }
        itineraryRepository.deleteByTourIdAndTouristPlaceId(tourId, placeId);

        List<Itinerary> remaining = itineraryRepository.findByTourIdOrderByPositionAsc(tourId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition(i + 1);
        }
        itineraryRepository.saveAll(remaining);

        return toResponse(tourRepository.findById(tourId)
        .orElseThrow(() -> new TourNotFoundException(tourId)));
    }

    // ── DISCOUNTS ──────────────────────────────────

    @Transactional
    public TourOfferResponse addDiscount(Long tourId, DiscountRequest request) {
        if (request.getPercentage() <= 0 || request.getPercentage() > 100) {
            throw new InvalidTourDataException("El porcentaje de descuento debe estar entre 1 y 100");
        }

        TourOffer offer = tourOfferRepository.findByTourId(tourId)
                .orElseThrow(() -> new TourOfferNotFoundException(tourId));
        UserType userType = userTypeRepository.findById(request.getUserTypeId())
                .orElseThrow(() -> new UserTypeNotFoundException(request.getUserTypeId()));

        boolean duplicated = offer.getDiscounts().stream()
                .anyMatch(d -> d.getUserType().getId().equals(request.getUserTypeId()));
        if (duplicated) {
            throw new InvalidTourDataException(
                    "Ya existe un descuento para el tipo de usuario con id: " + request.getUserTypeId());
        }

        Discount discount = new Discount();
        discount.setUserType(userType);
        discount.setPercentage(request.getPercentage());
        discount.setTourOffer(offer);
        offer.getDiscounts().add(discount);

        tourOfferRepository.save(offer);
        return toOfferResponse(offer);
    }

    @Transactional
    public TourOfferResponse removeDiscount(Long tourId, Long discountId) {
        TourOffer offer = tourOfferRepository.findByTourId(tourId)
                .orElseThrow(() -> new TourOfferNotFoundException(tourId));

        boolean removed = offer.getDiscounts().removeIf(d -> d.getId().equals(discountId));
        if (!removed) {
            throw new InvalidTourDataException(
                    "Descuento con id " + discountId + " no encontrado en el tour con id: " + tourId);
        }

        tourOfferRepository.save(offer);
        return toOfferResponse(offer);
    }

    // ── MAPPERS ────────────────────────────────────

    public TourResponse toResponse(Tour tour) {
        TourResponse response = new TourResponse();
        response.setId(tour.getId());
        response.setName(tour.getName());
        response.setDescription(tour.getDescription());
        response.setRecommendations(tour.getRecommendations());
        response.setPrice(tour.getPrice());

        if (tour.getEnvironment() != null)
            response.setEnvironment(tour.getEnvironment().name());
        if (tour.getLocation() != null)
            response.setLocation(tour.getLocation().getFullLocation());
        if (tour.getMeetingPoint() != null)
            response.setMeetingPoint(tour.getMeetingPoint().getFullLocation());

        response.setCategories(
                tour.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toList()));

        List<Itinerary> itinerary = itineraryRepository.findByTourIdOrderByPositionAsc(tour.getId());
        response.setItinerary(
                itinerary.stream()
                        .map(i -> new ItineraryItemResponse(
                                i.getId(),
                                i.getPosition(),
                                i.getTouristPlaceId(),
                                null))
                        .collect(Collectors.toList()));

        if (tour.getTourOffer() != null)
            response.setTourOffer(toOfferResponse(tour.getTourOffer()));

        // Álbum
        if (tour.getAlbum() != null) {
            List<PhotoResponse> photos = tour.getAlbum().getPhotos().stream()
                    .map(p -> new PhotoResponse(p.getFilePath(), p.getFileName(), p.getDescription()))
                    .collect(Collectors.toList());

            PhotoResponse current = tour.getAlbum().getCurrent() != null
                    ? new PhotoResponse(
                            tour.getAlbum().getCurrent().getFilePath(),
                            tour.getAlbum().getCurrent().getFileName(),
                            tour.getAlbum().getCurrent().getDescription())
                    : null;

            response.setAlbum(new AlbumResponse(
                    tour.getAlbum().getCurrentIndex(),
                    tour.getAlbum().getPhotos().size(),
                    current,
                    photos));
        }

        return response;
    }

    private TourOfferResponse toOfferResponse(TourOffer offer) {
        List<DiscountResponse> discounts = offer.getDiscounts().stream()
                .map(d -> new DiscountResponse(d.getId(), d.getUserType().getName(), d.getPercentage()))
                .collect(Collectors.toList());
        return new TourOfferResponse(offer.getId(), offer.getBasePrice(), discounts);
    }

    @Transactional(readOnly = true)
    public List<TourResponse> filterTours(TourFilterRequest filters) {
        Specification<Tour> spec = TourSpecification.withFilters(filters);
        return tourRepository.findAll(spec).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
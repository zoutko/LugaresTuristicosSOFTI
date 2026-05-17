package com.proyecto.app.tourManagment.controller;

import com.proyecto.app.tourManagment.dto.*;
import com.proyecto.app.tourManagment.dto.request.CreateTourRequest;
import com.proyecto.app.tourManagment.dto.request.DiscountRequest;
import com.proyecto.app.tourManagment.dto.request.UpdateTourRequest;
import com.proyecto.app.tourManagment.dto.response.TourOfferResponse;
import com.proyecto.app.tourManagment.dto.response.TourResponse;
import com.proyecto.app.tourManagment.service.TourService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    // ── TOUR CRUD ──────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TourResponse> createTour(@RequestBody CreateTourRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.createTour(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'USER', 'VISITOR')")
    public ResponseEntity<List<TourResponse>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/{tourId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'USER', 'VISITOR')")
    public ResponseEntity<TourResponse> getTourById(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourService.getTourById(tourId));
    }

    @PatchMapping("/{tourId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TourResponse> updateTour(
            @PathVariable Long tourId,
            @RequestBody UpdateTourRequest request) {
        return ResponseEntity.ok(tourService.updateTour(tourId, request));
    }

    @DeleteMapping("/{tourId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteTour(@PathVariable Long tourId) {
        tourService.deleteTour(tourId);
        return ResponseEntity.noContent().build();
    }

    // ── ITINERARY ──────────────────────────────────

    @PostMapping("/{tourId}/itinerary/{placeId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TourResponse> addPlaceToItinerary(
            @PathVariable Long tourId,
            @PathVariable Long placeId) {
        return ResponseEntity.ok(tourService.addPlaceToItinerary(tourId, placeId));
    }

    @DeleteMapping("/{tourId}/itinerary/{placeId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TourResponse> removePlaceFromItinerary(
            @PathVariable Long tourId,
            @PathVariable Long placeId) {
        return ResponseEntity.ok(tourService.removePlaceFromItinerary(tourId, placeId));
    }

    // ── DISCOUNTS ──────────────────────────────────

    @PostMapping("/{tourId}/discounts")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TourOfferResponse> addDiscount(
            @PathVariable Long tourId,
            @RequestBody DiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.addDiscount(tourId, request));
    }

    @DeleteMapping("/{tourId}/discounts/{discountId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TourOfferResponse> removeDiscount(
            @PathVariable Long tourId,
            @PathVariable Long discountId) {
        return ResponseEntity.ok(tourService.removeDiscount(tourId, discountId));
    }
}
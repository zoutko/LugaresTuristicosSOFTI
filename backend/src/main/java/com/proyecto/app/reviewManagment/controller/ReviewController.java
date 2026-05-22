package com.proyecto.app.reviewManagment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.app.reviewManagment.dto.request.CreateReviewRequest;
import com.proyecto.app.reviewManagment.dto.request.UpdateReviewRequest;
import com.proyecto.app.reviewManagment.dto.response.ReviewResponse;
import com.proyecto.app.reviewManagment.service.ReviewService;

@RestController
@RequestMapping("/api/tours/{tourId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewResponse>> getByTour(@PathVariable Long tourId) {
        return ResponseEntity.ok(reviewService.getReviewsByTour(tourId));
    }

    @GetMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> getById(
            @PathVariable Long tourId,
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> create(
            @PathVariable Long tourId,
            @RequestParam Long authorId,
            @RequestBody CreateReviewRequest request) {
        request.setTourId(tourId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(authorId, request));
    }

    @PatchMapping("/{reviewId}/user/{requesterId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long tourId,
            @PathVariable Long reviewId,
            @PathVariable Long requesterId,
            @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, requesterId, request));
    }

    @DeleteMapping("/{reviewId}/user/{requesterId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(
            @PathVariable Long tourId,
            @PathVariable Long reviewId,
            @PathVariable Long requesterId) {
        reviewService.deleteReview(reviewId, requesterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewResponse>> getByUser(
            @PathVariable Long tourId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }
}
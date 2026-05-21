package com.proyecto.app.reviewManagment.service;

import com.proyecto.app.common.User;
import com.proyecto.app.reviewManagment.domain.Review;
import com.proyecto.app.reviewManagment.dto.request.CreateReviewRequest;
import com.proyecto.app.reviewManagment.dto.request.UpdateReviewRequest;
import com.proyecto.app.reviewManagment.dto.response.ReviewResponse;
import com.proyecto.app.reviewManagment.exception.*;
import com.proyecto.app.reviewManagment.repository.ReviewRepository;
import com.proyecto.app.tourManagment.api.TourQueryService;
import com.proyecto.app.userManagment.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TourQueryService tourQueryService;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         TourQueryService tourQueryService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.tourQueryService = tourQueryService;
    }

    public ReviewResponse createReview(Long authorId, CreateReviewRequest request) {

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InvalidReviewException("El rating debe estar entre 1 y 5");
        }

        if (!tourQueryService.exists(request.getTourId())) {
            throw new InvalidReviewException("El recorrido con id " + request.getTourId() + " no existe");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new InvalidReviewException("Usuario no encontrado"));

        Review review = new Review(author, request.getTourId(),
                                   request.getRating(), request.getComment());
        return toResponse(reviewRepository.save(review));
    }

    public ReviewResponse updateReview(Long reviewId, Long requesterId, UpdateReviewRequest request) {
        Review review = findOrThrow(reviewId);
        verifyAuthor(review, requesterId);

        if (request.getRating() != null) {
            if (request.getRating() < 1 || request.getRating() > 5)
                throw new InvalidReviewException("El rating debe estar entre 1 y 5");
            review.setRating(request.getRating());
        }
        if (request.getComment() != null && !request.getComment().isBlank()) {
            review.setComment(request.getComment());
        }
        return toResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long reviewId, Long requesterId) {
        Review review = findOrThrow(reviewId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"));

        if (!isAdmin) {
            verifyAuthor(review, requesterId);
        }
        reviewRepository.delete(review);
    }

    public ReviewResponse getReview(Long reviewId) {
        return toResponse(findOrThrow(reviewId));
    }

    public List<ReviewResponse> getReviewsByTour(Long tourId) {
        return reviewRepository.findByTourId(tourId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByAuthorId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Review findOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    private void verifyAuthor(Review review, Long requesterId) {
        if (!review.getAuthor().getId().equals(requesterId)) {
            throw new UnauthorizedReviewActionException("modificar o eliminar");
        }
    }

    private ReviewResponse toResponse(Review review) {
        User author = review.getAuthor();
        String name = author.getUserProfile().getName();
        return new ReviewResponse(
                review.getId(),
                author.getId(),
                name,
                review.getTourId(),
                review.getRating(),
                review.getPublicationDate(),
                review.getComment()
        );
    }
}
package com.proyecto.test.reviewManagment;

import com.proyecto.app.common.User;
import com.proyecto.app.reviewManagment.domain.Review;
import com.proyecto.app.reviewManagment.dto.request.CreateReviewRequest;
import com.proyecto.app.reviewManagment.dto.response.ReviewResponse;
import com.proyecto.app.reviewManagment.dto.request.UpdateReviewRequest;
import com.proyecto.app.reviewManagment.exception.InvalidReviewException;
import com.proyecto.app.reviewManagment.exception.ReviewNotFoundException;
import com.proyecto.app.reviewManagment.exception.UnauthorizedReviewActionException;
import com.proyecto.app.reviewManagment.repository.ReviewRepository;
import com.proyecto.app.reviewManagment.service.ReviewService;
import com.proyecto.app.tourManagment.api.TourQueryService;
import com.proyecto.app.userManagment.domain.UserProfile;
import com.proyecto.app.userManagment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private TourQueryService tourQueryService;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private ReviewService reviewService;

    private User author;
    private Review review;
    private CreateReviewRequest createRequest;

    @BeforeEach
    void setUp() {
        UserProfile profile = new UserProfile();
        profile.setName("Juan Perez");
        
        author = new User();
        author.setId(1L);
        author.setUserProfile(profile);

        review = new Review(author, 10L, 4, "Excelente recorrido");

        try {
            var field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(review, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        createRequest = new CreateReviewRequest();
        createRequest.setTourId(10L);
        createRequest.setRating(4);
        createRequest.setComment("Excelente recorrido");
    }

    @Nested
    @DisplayName("createReview")
    class CreateReview {

        @Test
        @DisplayName("Crea reseña exitosamente")
        void createReview_success() {
            when(tourQueryService.exists(10L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(author));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            ReviewResponse response = reviewService.createReview(1L, createRequest);

            assertNotNull(response);
            assertEquals(4, response.getRating());
            assertEquals("Excelente recorrido", response.getComment());
            assertEquals(10L, response.getTourId());
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("Falla si rating es menor a 1")
        void createReview_invalidRatingLow() {
            createRequest.setRating(0);

            assertThrows(InvalidReviewException.class,
                    () -> reviewService.createReview(1L, createRequest));
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si rating es mayor a 5")
        void createReview_invalidRatingHigh() {
            createRequest.setRating(6);

            assertThrows(InvalidReviewException.class,
                    () -> reviewService.createReview(1L, createRequest));
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si el tour no existe")
        void createReview_tourNotFound() {
            when(tourQueryService.exists(10L)).thenReturn(false);

            assertThrows(InvalidReviewException.class,
                    () -> reviewService.createReview(1L, createRequest));
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si el usuario no existe")
        void createReview_userNotFound() {
            when(tourQueryService.exists(10L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(InvalidReviewException.class,
                    () -> reviewService.createReview(1L, createRequest));
            verify(reviewRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateReview")
    class UpdateReview {

        private UpdateReviewRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = new UpdateReviewRequest();
        }

        @Test
        @DisplayName("Edita reseña exitosamente siendo el autor")
        void updateReview_success() {
            updateRequest.setRating(5);
            updateRequest.setComment("Increíble experiencia");

            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            ReviewResponse response = reviewService.updateReview(1L, 1L, updateRequest);

            assertNotNull(response);
            verify(reviewRepository).save(review);
        }

        @Test
        @DisplayName("Falla si no es el autor")
        void updateReview_notAuthor() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            assertThrows(UnauthorizedReviewActionException.class,
                    () -> reviewService.updateReview(1L, 99L, updateRequest));
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si la reseña no existe")
        void updateReview_reviewNotFound() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.updateReview(1L, 1L, updateRequest));
        }

        @Test
        @DisplayName("Falla si el nuevo rating es inválido")
        void updateReview_invalidRating() {
            updateRequest.setRating(0);
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            assertThrows(InvalidReviewException.class,
                    () -> reviewService.updateReview(1L, 1L, updateRequest));
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("Actualiza solo el comentario si rating es null")
        void updateReview_onlyComment() {
            updateRequest.setComment("Nuevo comentario");

            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            reviewService.updateReview(1L, 1L, updateRequest);

            assertEquals("Nuevo comentario", review.getComment());
            assertEquals(4, review.getRating());
        }
    }

    @Nested
    @DisplayName("deleteReview")
    class DeleteReview {

        @Test
        @DisplayName("El autor puede eliminar su reseña")
        void deleteReview_byAuthor() {
            mockSecurityContext("ROLE_USER");
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            reviewService.deleteReview(1L, 1L);

            verify(reviewRepository).delete(review);
        }

        @Test
        @DisplayName("El administrador puede eliminar cualquier reseña")
        void deleteReview_byAdmin() {
            mockSecurityContext("ROLE_ADMINISTRATOR");
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            reviewService.deleteReview(1L, 99L);

            verify(reviewRepository).delete(review);
        }

        @Test
        @DisplayName("Falla si no es el autor ni administrador")
        void deleteReview_notAuthorNotAdmin() {
            mockSecurityContext("ROLE_USER");
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            assertThrows(UnauthorizedReviewActionException.class,
                    () -> reviewService.deleteReview(1L, 99L));
            verify(reviewRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Falla si la reseña no existe")
        void deleteReview_reviewNotFound() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.deleteReview(1L, 1L));
        }
    }

    @Nested
    @DisplayName("getReview")
    class GetReview {

        @Test
        @DisplayName("Obtiene reseña por id")
        void getReview_success() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            ReviewResponse response = reviewService.getReview(1L);

            assertNotNull(response);
            assertEquals(1L, response.getId());
        }

        @Test
        @DisplayName("Falla si la reseña no existe")
        void getReview_notFound() {
            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.getReview(1L));
        }

        @Test
        @DisplayName("Obtiene todas las reseñas de un tour")
        void getReviewsByTour_success() {
            when(reviewRepository.findByTourId(10L)).thenReturn(List.of(review));

            List<ReviewResponse> responses = reviewService.getReviewsByTour(10L);

            assertEquals(1, responses.size());
            assertEquals(10L, responses.get(0).getTourId());
        }

        @Test
        @DisplayName("Retorna lista vacía si el tour no tiene reseñas")
        void getReviewsByTour_empty() {
            when(reviewRepository.findByTourId(10L)).thenReturn(List.of());

            List<ReviewResponse> responses = reviewService.getReviewsByTour(10L);

            assertTrue(responses.isEmpty());
        }

        @Test
        @DisplayName("Obtiene todas las reseñas de un usuario")
        void getReviewsByUser_success() {
            when(reviewRepository.findByAuthorId(1L)).thenReturn(List.of(review));

            List<ReviewResponse> responses = reviewService.getReviewsByUser(1L);

            assertEquals(1, responses.size());
            assertEquals(1L, responses.get(0).getAuthorId());
        }
    }

    private void mockSecurityContext(String role) {
        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(role));

        doReturn(authorities).when(authentication).getAuthorities();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
package com.proyecto.app.reviewManagment.repository;

import com.proyecto.app.reviewManagment.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTourId(Long tourId);
    List<Review> findByAuthorId(Long authorId);
    boolean existsByAuthorIdAndTourId(Long authorId, Long tourId);
}

package com.proyecto.app.userManagment.repository;

import com.proyecto.app.userManagment.domain.SavedTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedTourRepository extends JpaRepository<SavedTour, Long> {
    List<SavedTour> findAllByUserId(Long userId);
    boolean existsByUserIdAndTourId(Long userId, Long tourId);
    void deleteByUserIdAndTourId(Long userId, Long tourId);
}

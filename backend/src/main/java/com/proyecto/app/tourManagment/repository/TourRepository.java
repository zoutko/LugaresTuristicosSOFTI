package com.proyecto.app.tourManagment.repository;

import com.proyecto.app.tourManagment.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByNameContainingIgnoreCase(String name);
}

package com.proyecto.app.tourManagment.repository;

import com.proyecto.app.tourManagment.domain.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByTourIdOrderByPositionAsc(Long tourId);
    void deleteByTourIdAndTouristPlaceId(Long tourId, Long touristPlaceId);
}

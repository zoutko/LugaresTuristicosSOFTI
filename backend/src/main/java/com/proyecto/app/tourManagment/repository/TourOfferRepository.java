package com.proyecto.app.tourManagment.repository;

import com.proyecto.app.tourManagment.domain.TourOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TourOfferRepository extends JpaRepository<TourOffer, Long> {
    Optional<TourOffer> findByTourId(Long tourId);
}

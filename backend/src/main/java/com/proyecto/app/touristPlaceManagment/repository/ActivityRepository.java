package com.proyecto.app.touristPlaceManagment.repository;

import com.proyecto.app.touristPlaceManagment.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByTouristPlaceId(Long placeId);
}

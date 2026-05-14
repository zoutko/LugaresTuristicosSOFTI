package com.proyecto.app.touristPlaceManagment.repository;

import com.proyecto.app.touristPlaceManagment.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    List<Activity> findByTouristPlaceId(UUID placeId);
}

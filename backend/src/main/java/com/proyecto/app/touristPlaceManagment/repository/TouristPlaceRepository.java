package com.proyecto.app.touristPlaceManagment.repository;

import com.proyecto.app.common.Environment;
import com.proyecto.app.touristPlaceManagment.domain.TouristPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TouristPlaceRepository extends JpaRepository<TouristPlace, UUID> {

    List<TouristPlace> findByNameContainingIgnoreCase(String name);

    List<TouristPlace> findByLocationCityIgnoreCase(String city);

    List<TouristPlace> findByEnvironment(Environment environment);

    // Busca por categoría usando la tabla join
    List<TouristPlace> findByCategoriesId(Long categoryId);
}

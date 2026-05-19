package com.proyecto.app.tourManagment.repository;

import com.proyecto.app.catalog.domain.Category;
import com.proyecto.app.common.Environment;
import com.proyecto.app.tourManagment.domain.Tour;
import com.proyecto.app.tourManagment.dto.request.TourFilterRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TourSpecification {

    public static Specification<Tour> withFilters(TourFilterRequest filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nombre (contains, case-insensitive)
            if (filters.getName() != null && !filters.getName().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + filters.getName().toLowerCase() + "%"
                ));
            }

            // Filtro por entorno (OR entre los seleccionados)
            if (filters.getEnvironments() != null && !filters.getEnvironments().isEmpty()) {
                List<Environment> envs = filters.getEnvironments().stream()
                    .map(Environment::valueOf)
                    .toList();
                predicates.add(root.get("environment").in(envs));
            }

            // Filtro por precio máximo
            if (filters.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filters.getMaxPrice()));
            }

            // Filtro por categorías (el tour debe tener AL MENOS una de las categorías)
            if (filters.getCategoryIds() != null && !filters.getCategoryIds().isEmpty()) {
                Join<Tour, Category> categoryJoin = root.join("categories", JoinType.INNER);
                predicates.add(categoryJoin.get("id").in(filters.getCategoryIds()));
                query.distinct(true); // evitar duplicados por el join
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

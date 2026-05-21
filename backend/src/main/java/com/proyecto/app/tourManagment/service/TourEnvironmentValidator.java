// tourManagment/service/TourEnvironmentValidator.java
package com.proyecto.app.tourManagment.service;

import com.proyecto.app.common.Environment;
import com.proyecto.app.touristPlaceManagment.api.PlaceQueryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TourEnvironmentValidator {

    private final PlaceQueryService placeQueryService;

    public TourEnvironmentValidator(PlaceQueryService placeQueryService) {
        this.placeQueryService = placeQueryService;
    }

    public Environment inferEnvironment(List<Long> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) return null;

        Set<Environment> envs = placeIds.stream()
                .map(placeQueryService::getEnvironment)
                .collect(Collectors.toSet());

        if (envs.size() == 1) return envs.iterator().next();
        return Environment.MIXED;
    }

    public void validateConsistency(Environment declared, List<Long> placeIds) {
        if (declared == null || placeIds == null || placeIds.isEmpty()) return;

        Environment inferred = inferEnvironment(placeIds);
        if (inferred != null && !declared.equals(inferred)) {
            throw new IllegalArgumentException(
                "El environment declarado (" + declared + ") no coincide con el de los lugares " +
                "del itinerario (inferido: " + inferred + ")"
            );
        }
    }
}
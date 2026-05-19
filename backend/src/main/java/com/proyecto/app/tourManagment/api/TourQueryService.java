package com.proyecto.app.tourManagment.api;

import com.proyecto.app.tourManagment.dto.response.TourResponse;
import java.util.List;

public interface TourQueryService {
    TourResponse findById(Long tourId);
    List<TourResponse> findAllByIds(List<Long> tourIds);
    boolean exists(Long tourId);
}

package com.proyecto.app.tourManagment.service;

import com.proyecto.app.tourManagment.api.TourQueryService;
import com.proyecto.app.tourManagment.dto.response.TourResponse;
import com.proyecto.app.tourManagment.repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TourQueryServiceImpl implements TourQueryService {

    private final TourRepository tourRepository;
    private final TourService tourService;

    public TourQueryServiceImpl(TourRepository tourRepository, TourService tourService) {
        this.tourRepository = tourRepository;
        this.tourService = tourService;
    }

    @Override
    @Transactional(readOnly = true)
    public TourResponse findById(Long tourId) {
        return tourService.getTourById(tourId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourResponse> findAllByIds(List<Long> tourIds) {
        return tourRepository.findAllById(tourIds).stream()
                .map(tourService::toResponse)
                .toList();
    }

    @Override
    public boolean exists(Long tourId) {
        return tourRepository.existsById(tourId);
    }
}

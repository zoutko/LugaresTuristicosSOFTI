package com.proyecto.app.tourManagment.exception;

public class TourOfferNotFoundException extends RuntimeException {
    public TourOfferNotFoundException(Long tourId) {
        super("TourOffer no encontrada para el tour con id: " + tourId);
    }
}

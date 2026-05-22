package com.proyecto.app.tourManagment.exception;

public class TourNotFoundException extends RuntimeException {
    public TourNotFoundException(Long id) {
        super("Tour no encontrado con id: " + id);
    }
}

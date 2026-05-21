package com.proyecto.app.touristPlaceManagment.exception;

public class TouristPlaceNotFoundException extends RuntimeException {
    public TouristPlaceNotFoundException(String id) {
        super("Lugar turístico no encontrado: " + id);
    }
}

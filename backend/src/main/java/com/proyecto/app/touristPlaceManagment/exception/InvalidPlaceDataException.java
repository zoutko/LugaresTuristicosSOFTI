package com.proyecto.app.touristPlaceManagment.exception;

/**
 * Lanzada cuando el request de TouristPlace contiene datos inválidos
 * que @Valid no cubre (validaciones de negocio).
 */
public class InvalidPlaceDataException extends RuntimeException {
    public InvalidPlaceDataException(String message) {
        super(message);
    }
}

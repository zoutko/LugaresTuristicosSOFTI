package com.proyecto.app.reviewManagment.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long id) {
        super("Reseña no encontrada con id: " + id);
    }
}

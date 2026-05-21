package com.proyecto.app.reviewManagment.exception;

public class UnauthorizedReviewActionException extends RuntimeException {
    public UnauthorizedReviewActionException(String action) {
        super("No tienes permiso para " + action + " esta reseña");
    }
}
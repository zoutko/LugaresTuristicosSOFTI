package com.proyecto.app.media.exception;

public class InvalidPhotoException extends RuntimeException {
    public InvalidPhotoException(String message) {
        super("La foto es inválida: " + message);
    }
}

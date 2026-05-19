package com.proyecto.app.media.exception;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException(String message) {
        super("Media no encontrada: " + message);
    }
}

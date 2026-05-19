package com.proyecto.app.media.exception;

public class InvalidAlbumOperationException extends RuntimeException {
    public InvalidAlbumOperationException(String message) {
        super("Operación inválida en el álbum: " + message);
    }
}

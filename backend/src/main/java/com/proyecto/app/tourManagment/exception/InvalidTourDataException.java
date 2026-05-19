package com.proyecto.app.tourManagment.exception;

public class InvalidTourDataException extends RuntimeException {
    public InvalidTourDataException(String message) {
        super(message);
    }
}
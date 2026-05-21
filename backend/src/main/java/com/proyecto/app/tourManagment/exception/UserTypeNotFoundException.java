package com.proyecto.app.tourManagment.exception;

public class UserTypeNotFoundException extends RuntimeException {
    public UserTypeNotFoundException(Long id) {
        super("UserType no encontrado con id: " + id);
    }
}
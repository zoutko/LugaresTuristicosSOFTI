package com.proyecto.app.touristPlaceManagment.exception;

public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(int activityId) {
        super("Actividad no encontrada: " + activityId);
    }
}

package com.proyecto.app.touristPlaceManagment.exception;

// ── Entidad no encontrada ────────────────────────────────────────────────────

/**
 * Lanzada cuando no se encuentra un TouristPlace por id.
 */
public class TouristPlaceNotFoundException extends RuntimeException {
    public TouristPlaceNotFoundException(String id) {
        super("Lugar turístico no encontrado: " + id);
    }
}

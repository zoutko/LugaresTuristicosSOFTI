package com.proyecto.test.userManagment.domain;

import java.util.Set;

public class UserRole implements Role {

    private static final String ROLE_NAME = "USER";

    private static final Set<String> PERMISSIONS = Set.of(
            // Recorridos y lugares (solo lectura)
            "READ_ROUTE",
            "READ_PLACE",
            // Reservas
            "CREATE_RESERVATION",
            "CANCEL_RESERVATION",
            // Reseñas (solo si hizo el recorrido; validación en servicio)
            "CREATE_REVIEW",
            "UPDATE_OWN_REVIEW",
            "DELETE_OWN_REVIEW",
            // Perfil
            "VIEW_PROFILE",
            "UPDATE_PROFILE",
            "DELETE_ACCOUNT"
    );

    @Override
    public String getNameRole() {
        return ROLE_NAME;
    }

    @Override
    public Set<String> getPermissions() {
        return PERMISSIONS;
    }
}
package com.proyecto.app.userManagment.domain;
import java.util.Set;

public class Administrator implements Role {

    private static final String ROLE_NAME = "ADMINISTRATOR";

    private static final Set<String> PERMISSIONS = Set.of(
            // Recorridos
            "CREATE_ROUTE",
            "READ_ROUTE",
            "UPDATE_ROUTE",
            "DELETE_ROUTE",
            // Lugares
            "CREATE_PLACE",
            "READ_PLACE",
            "UPDATE_PLACE",
            "DELETE_PLACE",
            // Reseñas
            "READ_REVIEW",
            "DELETE_REVIEW",
            // Perfil propio
            "VIEW_PROFILE",
            "UPDATE_PROFILE"
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
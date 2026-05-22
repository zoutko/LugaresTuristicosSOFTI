package com.proyecto.app.userManagment.domain;
import java.util.Set;

public class Administrator implements Role {

    private static final String ROLE_NAME = "ADMINISTRATOR";

    private static final Set<String> PERMISSIONS = Set.of(
            "CREATE_ROUTE",
            "READ_ROUTE",
            "UPDATE_ROUTE",
            "DELETE_ROUTE",
            "CREATE_PLACE",
            "READ_PLACE",
            "UPDATE_PLACE",
            "DELETE_PLACE",
            "READ_REVIEW",
            "DELETE_REVIEW",
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
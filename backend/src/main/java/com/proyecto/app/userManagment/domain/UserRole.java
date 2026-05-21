package com.proyecto.app.userManagment.domain;

import java.util.Set;

public class UserRole implements Role {

    private static final String ROLE_NAME = "USER";

    private static final Set<String> PERMISSIONS = Set.of(
            "READ_ROUTE",
            "READ_PLACE",
            "CREATE_RESERVATION",
            "CANCEL_RESERVATION",
            "CREATE_REVIEW",
            "UPDATE_OWN_REVIEW",
            "DELETE_OWN_REVIEW",
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
package com.proyecto.test.userManagment.domain;


import java.util.Set;

public class Visitor implements Role {

    private static final String ROLE_NAME = "VISITOR";

    private static final Set<String> PERMISSIONS = Set.of(
            "READ_ROUTE",
            "READ_PLACE",
            "CREATE_ACCOUNT"
    );

    @Override
    public String getNameRole() {
        return ROLE_NAME;
    }

    @Override
    public Set<String> getPermissions() {
        return PERMISSIONS;
    }

    public boolean createdAccount() {
        return true;
    }
}
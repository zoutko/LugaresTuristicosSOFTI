package com.proyecto.app.security.dto.response;

import java.util.Set;

public class RoleDTO {

    private String name;
    private Set<String> permissions;

    public RoleDTO() {}

    public RoleDTO(String name, Set<String> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
}
package com.proyecto.test.security.dto.response;

import java.util.Set;

public class RoleDTO {

    private String name;             // "ADMIN", "USER", "GUIDE"
    private Set<String> permissions; // ["READ", "WRITE", "DELETE"]

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
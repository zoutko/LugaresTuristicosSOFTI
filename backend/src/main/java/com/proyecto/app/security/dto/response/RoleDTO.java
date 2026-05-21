package com.proyecto.app.security.dto.response;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleDTO {

    private String name;
    private Set<String> permissions;


    public RoleDTO(String name, Set<String> permissions) {
        this.name = name;
        this.permissions = permissions;
    }
}
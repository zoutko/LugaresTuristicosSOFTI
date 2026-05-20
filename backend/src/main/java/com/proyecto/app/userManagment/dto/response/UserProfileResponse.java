package com.proyecto.app.userManagment.dto.response;

import com.proyecto.app.security.dto.response.RoleDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String name;
    private String document;
    private RoleDTO role;                  
    private List<ContactResponse> contacts;

    public UserProfileResponse(Long id, String name, String document,
                                RoleDTO role, List<ContactResponse> contacts) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.role = role;
        this.contacts = contacts;
    }
}
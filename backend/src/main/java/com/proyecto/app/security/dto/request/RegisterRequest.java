package com.proyecto.app.security.dto.request;

import com.proyecto.app.security.dto.response.RoleDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    private String document;
    private String phoneNumber;

    private String email;
    private String password;
    private Long userId;
    private RoleDTO role;


    public RegisterRequest(String email, String password, Long userId, RoleDTO role) {
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.role = role;
    }

}
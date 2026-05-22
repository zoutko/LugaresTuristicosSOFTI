package com.proyecto.app.security.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    private String email;
    private String password;


    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
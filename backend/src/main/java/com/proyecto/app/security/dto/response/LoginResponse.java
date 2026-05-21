package com.proyecto.app.security.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String type;
    private String email;
    private String role;
    private Long userId;

    public LoginResponse(String token, String email, String role, Long userId) {
        this.token = token;
        this.type = "Bearer";
        this.email = email;
        this.role = role;
        this.userId = userId;
    }
}
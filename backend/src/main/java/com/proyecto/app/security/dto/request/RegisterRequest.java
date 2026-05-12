package com.proyecto.app.security.dto.request;

import com.proyecto.app.security.dto.response.RoleDTO;

public class RegisterRequest {

    private String email;
    private String password;
    private Long userId;
    private RoleDTO role;

    public RegisterRequest() {}

    public RegisterRequest(String email, String password, Long userId, RoleDTO role) {
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public RoleDTO getRole() { return role; }
    public void setRole(RoleDTO role) { this.role = role; }
}
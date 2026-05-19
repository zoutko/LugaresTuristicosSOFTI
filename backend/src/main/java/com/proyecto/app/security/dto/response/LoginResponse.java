package com.proyecto.app.security.dto.response;


public class LoginResponse {

    private String token;
    private String type;
    private String email;
    private String role;
    private Long userId;

    public LoginResponse() {}

    public LoginResponse(String token, String email, String role, Long userId) {
        this.token = token;
        this.type = "Bearer";
        this.email = email;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
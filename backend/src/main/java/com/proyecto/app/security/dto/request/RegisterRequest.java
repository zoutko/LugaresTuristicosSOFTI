package com.proyecto.app.security.dto.request;

import com.proyecto.app.security.dto.response.RoleDTO;

public class RegisterRequest {

    private String name;
    private String document;
    private String phoneNumber;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public RoleDTO getRole() { return role; }
    public void setRole(RoleDTO role) { this.role = role; }
}
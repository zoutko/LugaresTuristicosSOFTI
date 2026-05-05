package com.proyecto.test.security.dto.request;

public class RecoverPasswordRequest {

    private String email;

    public RecoverPasswordRequest() {}

    public RecoverPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
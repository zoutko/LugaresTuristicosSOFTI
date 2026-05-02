package com.proyecto.test.security.dto.response;

public class LoginResponse {

    private String token;
    private String tipo;      // siempre "Bearer"
    private String correo;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String token, String correo, String role) {
        this.token = token;
        this.tipo = "Bearer";
        this.correo = correo;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
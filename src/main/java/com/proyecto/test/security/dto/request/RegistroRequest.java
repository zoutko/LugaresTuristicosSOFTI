package com.proyecto.test.security.dto.request;

import com.proyecto.test.security.dto.response.RoleDTO;

public class RegistroRequest {

    private String correo;
    private String contrasena;
    private Long userId;    // referencia al User de UserManagement
    private RoleDTO role;   // rol que viene desde UserManagement

    public RegistroRequest() {}

    public RegistroRequest(String correo, String contrasena, Long userId, RoleDTO role) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.userId = userId;
        this.role = role;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public RoleDTO getRole() { return role; }
    public void setRole(RoleDTO role) { this.role = role; }
}
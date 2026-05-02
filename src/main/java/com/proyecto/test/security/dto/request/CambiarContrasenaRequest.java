package com.proyecto.test.security.dto.request;

public class CambiarContrasenaRequest {

    private String correo;
    private String contrasenaActual;  // se valida antes de cambiar
    private String nuevaContrasena;

    public CambiarContrasenaRequest() {}

    public CambiarContrasenaRequest(String correo, String contrasenaActual, String nuevaContrasena) {
        this.correo = correo;
        this.contrasenaActual = contrasenaActual;
        this.nuevaContrasena = nuevaContrasena;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasenaActual() { return contrasenaActual; }
    public void setContrasenaActual(String contrasenaActual) { this.contrasenaActual = contrasenaActual; }

    public String getNuevaContrasena() { return nuevaContrasena; }
    public void setNuevaContrasena(String nuevaContrasena) { this.nuevaContrasena = nuevaContrasena; }
}
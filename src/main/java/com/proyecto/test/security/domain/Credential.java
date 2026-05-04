package com.proyecto.test.security.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "credentials")
public class Credential implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private State estado;

    // El rol llega como String desde UserManagement vía RoleDTO
    // Security no necesita saber la estructura interna de los roles
    private String role;

    // Referencia al módulo UserManagement (solo el ID, no el objeto completo)
    private Long userId;

    public Credential() {}

    public Credential(String correo, String contrasena, String role, Long userId) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.role = role;
        this.estado = State.ACTIVO;
        this.userId = userId;
    }

    // ── UserDetails (requeridos por Spring Security) ──────────────────────

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security usa "ROLE_" como prefijo estándar
        // Permite usar @PreAuthorize("hasRole('ADMIN')") en los controllers
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return estado != State.BLOQUEADO;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado == State.ACTIVO;
    }

    // ── Getters y Setters ─────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public State getEstado() { return estado; }
    public void setEstado(State estado) { this.estado = estado; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
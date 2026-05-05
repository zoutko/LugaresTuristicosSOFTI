package com.proyecto.test.security.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens_recuperacion")
public class TokenRecuperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Date fechaExpiracion;

    private boolean usado;

    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    public TokenRecuperation() {}

    public TokenRecuperation(String token, Date fechaExpiracion, Credential credential) {
        this.token = token;
        this.fechaExpiracion = fechaExpiracion;
        this.credential = credential;
        this.usado = false;
    }

    // ── Métodos de dominio ────────────────────────────────────────────────

    public boolean isExpirado() {
        return new Date().after(fechaExpiracion);
    }

    public boolean isValido() {
        return !usado && !isExpirado();
    }

    // ── Getters y Setters ─────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Date getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(Date fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }

    public Credential getCredential() { return credential; }
    public void setCredential(Credential credential) { this.credential = credential; }
}

package com.proyecto.test.security.domain;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String valor;

    @Column(nullable = false)
    private Date fechaExpiracion;

    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    private boolean revocado;

    public Token() {}

    public Token(String valor, Date fechaExpiracion, Credential credential) {
        this.valor = valor;
        this.fechaExpiracion = fechaExpiracion;
        this.credential = credential;
        this.revocado = false;
    }

    // ── Métodos de dominio ────────────────────────────────────────────────

    public boolean isExpirado() {
        return new Date().after(fechaExpiracion);
    }

    public boolean isValido() {
        return !revocado && !isExpirado();
    }

    // ── Getters y Setters ─────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public Date getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(Date fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public Credential getCredential() { return credential; }
    public void setCredential(Credential credential) { this.credential = credential; }

    public boolean isRevocado() { return revocado; }
    public void setRevocado(boolean revocado) { this.revocado = revocado; }
}
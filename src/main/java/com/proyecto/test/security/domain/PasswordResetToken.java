package com.proyecto.test.security.domain;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Date expirationDate;

    private boolean used;

    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, Date expirationDate, Credential credential) {
        this.token = token;
        this.expirationDate = expirationDate;
        this.credential = credential;
        this.used = false;
    }

    public boolean isExpired() {
        return new Date().after(expirationDate);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public Credential getCredential() { return credential; }
    public void setCredential(Credential credential) { this.credential = credential; }
}
package com.proyecto.app.security.domain;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String token;

    @Column(nullable = false)
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    private boolean revoked;

    public Token() {}

    public Token(String token, Date expirationDate, Credential credential) {
        this.token = token;
        this.expirationDate = expirationDate;
        this.credential = credential;
        this.revoked = false;
    }

    public boolean isExpired() {
        return new Date().after(expirationDate);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }

    public Credential getCredential() { return credential; }
    public void setCredential(Credential credential) { this.credential = credential; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}
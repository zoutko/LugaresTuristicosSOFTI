package com.proyecto.app.security.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 512)
    private String token;

    @Column(nullable = false)
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    private boolean revoked;

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
}
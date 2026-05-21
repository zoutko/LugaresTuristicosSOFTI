package com.proyecto.app.security.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
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

}
package com.proyecto.test.security.repository;

import com.proyecto.test.security.domain.TokenRecuperation;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRecuperationRepository extends JpaRepository<TokenRecuperation, Long> {

    Optional<TokenRecuperation> findByToken(String token);

    // Elimina tokens anteriores del mismo usuario antes de generar uno nuevo
    void deleteByCredentialId(Long credentialId);

}
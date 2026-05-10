package com.proyecto.test.userManagment.repository;

import com.proyecto.test.userManagment.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByDocument(String document);

    boolean existsByDocument(String document);
}

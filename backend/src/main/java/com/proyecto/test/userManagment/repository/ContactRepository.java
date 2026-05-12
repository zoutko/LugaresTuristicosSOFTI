package com.proyecto.test.userManagment.repository;

import com.proyecto.test.userManagment.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByUserProfileId(Long userProfileId);

    void deleteAllByUserProfileId(Long userProfileId);
}

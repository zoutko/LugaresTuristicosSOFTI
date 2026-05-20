package com.proyecto.app.userManagment.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String document;


    @Column(name = "role_name", nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Contact> contacts = new ArrayList<>();

    public UserProfile(String name, String document, String roleName) {
        this.name = name;
        this.document = document;
        this.roleName = roleName;
    }

    public void updateInformation(String field, String newValue) {
        switch (field.toLowerCase()) {
            case "name"     -> this.name = newValue;
            case "document" -> this.document = newValue;
            case "rolename" -> this.roleName = newValue;
            default -> throw new IllegalArgumentException("Campo no reconocido: " + field);
        }
    }

    public UserProfile getData() {
        return this;
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setUserProfile(this);
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setUserProfile(null);
    }
}
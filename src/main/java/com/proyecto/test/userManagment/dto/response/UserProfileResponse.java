package com.proyecto.test.userManagment.dto.response;

import com.proyecto.test.security.dto.response.RoleDTO;

import java.util.List;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String document;
    private RoleDTO role;                  
    private List<ContactResponse> contacts;

    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String name, String document,
                                RoleDTO role, List<ContactResponse> contacts) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.role = role;
        this.contacts = contacts;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }

    public RoleDTO getRole() { return role; }
    public void setRole(RoleDTO role) { this.role = role; }

    public List<ContactResponse> getContacts() { return contacts; }
    public void setContacts(List<ContactResponse> contacts) { this.contacts = contacts; }
}
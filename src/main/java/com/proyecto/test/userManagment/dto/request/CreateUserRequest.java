package com.proyecto.test.userManagment.dto.request;

import java.util.List;

public class CreateUserRequest {

    private String name;
    private String document;
    private String roleName;          
    private List<String> phoneNumbers; 

    private String email;
    private String password;

    public CreateUserRequest() {}

    public CreateUserRequest(String name, String document, String roleName,
                              List<String> phoneNumbers, String email, String password) {
        this.name = name;
        this.document = document;
        this.roleName = roleName;
        this.phoneNumbers = phoneNumbers;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public List<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(List<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

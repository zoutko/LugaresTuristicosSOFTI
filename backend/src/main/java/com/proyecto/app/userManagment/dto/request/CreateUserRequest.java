package com.proyecto.app.userManagment.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    private String name;
    private String document;
    private String roleName;          
    private List<String> phoneNumbers; 

    private String email;
    private String password;

    public CreateUserRequest(String name, String document, String roleName,
                              List<String> phoneNumbers, String email, String password) {
        this.name = name;
        this.document = document;
        this.roleName = roleName;
        this.phoneNumbers = phoneNumbers;
        this.email = email;
        this.password = password;
    }
}

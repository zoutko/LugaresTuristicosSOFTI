package com.proyecto.app.userManagment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    private String field;     
    private String newValue;

    public UpdateUserRequest(String field, String newValue) {
        this.field = field;
        this.newValue = newValue;
    }

}
package com.proyecto.app.security.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecoverPasswordRequest {

    private String email;

    public RecoverPasswordRequest(String email) {
        this.email = email;
    }
}
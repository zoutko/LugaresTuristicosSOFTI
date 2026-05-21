package com.proyecto.app.security.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeEmailRequest {
    private Long userId;
    private String currentPassword;
    private String newEmail;
}

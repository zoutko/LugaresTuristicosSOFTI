package com.proyecto.app.userManagment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private UserProfileResponse profile;


    public UserResponse(Long id, String email, UserProfileResponse profile) {
        this.id = id;
        this.email = email;
        this.profile = profile;
    }
}
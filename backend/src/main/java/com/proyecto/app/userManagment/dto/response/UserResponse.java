package com.proyecto.app.userManagment.dto.response;

public class UserResponse {

    private Long id;
    private String email;
    private UserProfileResponse profile;

    public UserResponse() {}

    public UserResponse(Long id, String email, UserProfileResponse profile) {
        this.id = id;
        this.email = email;
        this.profile = profile;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserProfileResponse getProfile() { return profile; }
    public void setProfile(UserProfileResponse profile) { this.profile = profile; }
}
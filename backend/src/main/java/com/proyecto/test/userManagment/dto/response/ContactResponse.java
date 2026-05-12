package com.proyecto.test.userManagment.dto.response;

public class ContactResponse {

    private Long id;
    private String phoneNumber;

    public ContactResponse() {}

    public ContactResponse(Long id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

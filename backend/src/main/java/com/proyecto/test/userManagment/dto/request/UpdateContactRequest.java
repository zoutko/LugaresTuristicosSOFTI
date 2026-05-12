package com.proyecto.test.userManagment.dto.request;

public class UpdateContactRequest {

    private Long contactId;      
    private String phoneNumber;

    public UpdateContactRequest() {}

    public UpdateContactRequest(Long contactId, String phoneNumber) {
        this.contactId = contactId;
        this.phoneNumber = phoneNumber;
    }

    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

package com.proyecto.app.userManagment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateContactRequest {

    private Long contactId;      
    private String phoneNumber;


    public UpdateContactRequest(Long contactId, String phoneNumber) {
        this.contactId = contactId;
        this.phoneNumber = phoneNumber;
    }
}

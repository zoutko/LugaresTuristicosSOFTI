package com.proyecto.app.userManagment.dto.request;

public class UpdateUserRequest {

    private String field;     
    private String newValue;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String field, String newValue) {
        this.field = field;
        this.newValue = newValue;
    }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
}
package com.proyecto.app.tourManagment.domain;

public class Rate {
    
    private UserType userType;
    private double price;
    public UserType getUserType() {
        return userType;
    }
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    
}

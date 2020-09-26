package com.rwtcompany.onlinevegitableshopapp.model;

public class UserDetails extends DeliveryDetails {
    private String email;

    public UserDetails() {
    }

    public UserDetails(String address, String name, String number, String email) {
        super(address, name, number);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

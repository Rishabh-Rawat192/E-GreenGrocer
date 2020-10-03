package com.rwtcompany.onlinevegitableshopapp.model;

public class AdminMetaData extends OrderExtraCharges {
    private String pin;
    private String email;
    private String token;

    public AdminMetaData(String minOrderPrice, String deliveryCharge, String pin, String email, String token) {
        super(minOrderPrice, deliveryCharge);
        this.pin = pin;
        this.email = email;
        this.token = token;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

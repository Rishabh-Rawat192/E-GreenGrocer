package com.rwtcompany.onlinevegitableshopapp.model;

public class UserDetails {
    private DeliveryDetails deliveryDetails;
    private String email;

    public UserDetails(DeliveryDetails deliveryDetails, String email) {
        this.deliveryDetails = deliveryDetails;
        this.email = email;
    }

    public DeliveryDetails getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(DeliveryDetails deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "deliveryDetails=" + deliveryDetails +
                ", email='" + email + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

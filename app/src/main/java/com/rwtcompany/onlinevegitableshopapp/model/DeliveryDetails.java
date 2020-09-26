package com.rwtcompany.onlinevegitableshopapp.model;

public class DeliveryDetails {
    private String address;
    private String name;
    private String number;

    public DeliveryDetails() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "DeliveryDetails{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public DeliveryDetails(String address, String name, String number){
        this.address=address;
        this.name=name;
        this.number=number;
    }
}

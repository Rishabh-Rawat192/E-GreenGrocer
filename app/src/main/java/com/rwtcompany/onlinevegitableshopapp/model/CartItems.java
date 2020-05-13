package com.rwtcompany.onlinevegitableshopapp.model;

public class CartItems {
    private String name;
    private String imageUrl;
    private String price;
    private String unit;
    private String quantity;
    private String cost;

    public CartItems()
    {}

    public CartItems(String name, String imageUrl, String price, String unit, String quantity, String cost) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.unit = unit;
        this.quantity = quantity;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}

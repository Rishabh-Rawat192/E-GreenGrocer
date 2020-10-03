package com.rwtcompany.onlinevegitableshopapp.model;

public class AdminItem {
    private String imageUrl;
    private String name;
    private String price;
    private String unit;
    public AdminItem()
    {}

    public AdminItem(String imageUrl, String name, String price, String unit) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.unit = unit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

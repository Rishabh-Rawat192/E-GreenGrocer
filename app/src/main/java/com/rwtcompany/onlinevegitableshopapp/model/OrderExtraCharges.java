package com.rwtcompany.onlinevegitableshopapp.model;

public class OrderExtraCharges {
    private String minOrderPrice;
    private String deliveryCharge;

    public OrderExtraCharges(String minOrderPrice, String deliveryCharge) {
        this.minOrderPrice = minOrderPrice;
        this.deliveryCharge = deliveryCharge;
    }

    @Override
    public String toString() {
        return "OrderExtraCharges{" +
                "minOrderPrice='" + minOrderPrice + '\'' +
                ", deliveryCharge='" + deliveryCharge + '\'' +
                '}';
    }

    public String getMinOrderPrice() {
        return minOrderPrice;
    }

    public void setMinOrderPrice(String minOrderPrice) {
        this.minOrderPrice = minOrderPrice;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}

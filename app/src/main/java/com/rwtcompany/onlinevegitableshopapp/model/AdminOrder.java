package com.rwtcompany.onlinevegitableshopapp.model;

public class AdminOrder extends UserOrder {
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public AdminOrder(String orderId, String totalCost, String orderStatus, String uuid) {
        super(orderId, totalCost, orderStatus);
        this.uuid = uuid;
    }
}

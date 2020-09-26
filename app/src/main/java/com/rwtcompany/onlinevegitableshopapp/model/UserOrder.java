package com.rwtcompany.onlinevegitableshopapp.model;

public class UserOrder {
    private String orderId;
    private String totalCost;
    private String orderStatus;

    public UserOrder(String orderId, String totalCost, String orderStatus) {
        this.orderId = orderId;
        this.totalCost = totalCost;
        this.orderStatus=orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    @Override
    public String toString() {
        return "UserOrdersList{" +
                "orderId='" + orderId + '\'' +
                ", totalCost='" + totalCost + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}

package com.rwtcompany.onlinevegitableshopapp.model;

public class OrderDetails extends DeliveryDetails {
    private String deliveryCharge;
    private String orderId;
    private String orderStatus;
    private String requestTime;
    private String total;

    public OrderDetails() {
    }

    public OrderDetails(String address, String name, String number, String deliveryCharge, String orderId, String orderStatus, String requestTime, String total) {
        super(address, name, number);
        this.deliveryCharge = deliveryCharge;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.requestTime = requestTime;
        this.total = total;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

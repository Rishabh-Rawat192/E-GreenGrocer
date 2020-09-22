package com.rwtcompany.onlinevegitableshopapp.model;

public class OrderDetails {
    private DeliveryDetails deliveryDetails;
    private String deliveryCharge;
    private String orderId;
    private String orderStatus;
    private String requestTime;
    private String total;

    public DeliveryDetails getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(DeliveryDetails deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
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

    public OrderDetails(DeliveryDetails deliveryDetails, String deliveryCharge, String orderId, String orderStatus, String requestTime, String total) {
        this.deliveryDetails = deliveryDetails;
        this.deliveryCharge = deliveryCharge;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.requestTime = requestTime;
        this.total = total;
    }
}

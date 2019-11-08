package com.judopay.model;

public class OrderDetails {
    private String orderId;
    private OrderStatus orderStatus;
    private String orderFailureReason;
    private String timestamp;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

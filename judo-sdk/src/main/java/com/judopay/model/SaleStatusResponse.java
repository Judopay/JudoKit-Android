package com.judopay.model;

public class SaleStatusResponse {
    private OrderDetails orderDetails;
    private String merchantPaymentReference;
    private String paymentMethod;
    private String siteId;
    private String merchantConsumerReference;

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }
}

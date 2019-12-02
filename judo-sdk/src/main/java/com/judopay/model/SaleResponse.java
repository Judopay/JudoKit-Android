package com.judopay.model;

public class SaleResponse {
    private String amount;
    private String redirectUrl;
    private String orderId;
    private String merchantPaymentReference;
    private String merchantName;
    private String merchantRedirectUrl;
    private String appearsOnStatementAs;
    private AccountDetails accountDetails;
    private String paymentMethod;
    private String siteId;
    private String currency;
    private SaleConsumer consumer;
    private String status;

    public String getAmount() {
        return amount;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantPaymentReference() {
        return merchantPaymentReference;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMerchantRedirectUrl() {
        return merchantRedirectUrl;
    }

    public String getAppearsOnStatementAs() {
        return appearsOnStatementAs;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getCurrency() {
        return currency;
    }

    public SaleConsumer getConsumer() {
        return consumer;
    }

    public String getStatus() {
        return status;
    }
}

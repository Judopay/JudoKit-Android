package com.judopay.model;

import java.util.Map;

public class SaleStatusRequest {
    private String orderId;
    private String merchantPaymentReference;
    private String checksum;
    private String paymentMethod;
    private String siteId;
    private Map<String, String> paymentMetadata;
    private String merchantConsumerReference;

    public SaleStatusRequest(String orderId, String merchantPaymentReference, String checksum, String paymentMethod, String siteId, Map<String, String> paymentMetadata, String merchantConsumerReference) {
        this.orderId = orderId;
        this.merchantPaymentReference = merchantPaymentReference;
        this.checksum = checksum;
        this.paymentMethod = paymentMethod;
        this.siteId = siteId;
        this.paymentMetadata = paymentMetadata;
        this.merchantConsumerReference = merchantConsumerReference;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantPaymentReference() {
        return merchantPaymentReference;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getMerchantConsumerReference() {
        return merchantConsumerReference;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}

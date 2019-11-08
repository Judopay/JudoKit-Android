package com.judopay.model;

import java.util.Map;

public class SaleRequest {
    private String country;
    private String amount;
    private String merchantPaymentReference;
    private Map<String, String> paymentMetadata;
    private String merchantConsumerReference;
    private String accountHolderName;
    private String paymentMethod;
    private String siteId;
    private String currency;
    private String bic;

    public String getCountry() {
        return country;
    }

    public String getAmount() {
        return amount;
    }

    public String getMerchantPaymentReference() {
        return merchantPaymentReference;
    }

    public String getMerchantConsumerReference() {
        return merchantConsumerReference;
    }

    public String getAccountHolderName() {
        return accountHolderName;
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

    public String getBic() {
        return bic;
    }
}

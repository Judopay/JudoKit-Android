package com.judopay.model;

import java.math.BigDecimal;
import java.util.Map;

public class SaleRequest {
    private String country;
    private BigDecimal amount;
    private String merchantPaymentReference;
    private Map<String, String> paymentMetadata;
    private String merchantConsumerReference;
    private String accountHolderName;
    private String paymentMethod;
    private String siteId;
    private String currency;
    private String bic;

    public SaleRequest(String country, BigDecimal amount, String merchantPaymentReference, Map<String, String> paymentMetadata, String merchantConsumerReference, String accountHolderName, String paymentMethod, String siteId, String currency, String bic) {
        this.country = country;
        this.amount = amount;
        this.merchantPaymentReference = merchantPaymentReference;
        this.paymentMetadata = paymentMetadata;
        this.merchantConsumerReference = merchantConsumerReference;
        this.accountHolderName = accountHolderName;
        this.paymentMethod = paymentMethod;
        this.siteId = siteId;
        this.currency = currency;
        this.bic = bic;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getAmount() {
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

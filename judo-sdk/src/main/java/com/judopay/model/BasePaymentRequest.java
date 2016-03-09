package com.judopay.model;

import com.judopay.api.Request;

import java.math.BigDecimal;
import java.util.Map;

abstract class BasePaymentRequest extends Request {

    BigDecimal amount;
    String currency;
    String judoId;
    String yourConsumerReference;
    Map<String, String> yourPaymentMetaData;

    protected BasePaymentRequest(String yourPaymentReference) {
        super(yourPaymentReference);
    }

    protected BasePaymentRequest(boolean uniqueRequest) {
        super(uniqueRequest);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getJudoId() {
        return judoId;
    }

    public String getYourConsumerReference() {
        return yourConsumerReference;
    }

    public Map<String, String> getMetaData() {
        return yourPaymentMetaData;
    }

}
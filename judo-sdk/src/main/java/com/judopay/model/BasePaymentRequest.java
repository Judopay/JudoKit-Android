package com.judopay.model;

import com.judopay.api.Request;

import java.util.Map;

@SuppressWarnings("unused")
abstract class BasePaymentRequest extends Request {

    String amount;
    String currency;
    String judoId;
    String yourConsumerReference;
    Map<String, String> yourPaymentMetaData;

    public String getAmount() {
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
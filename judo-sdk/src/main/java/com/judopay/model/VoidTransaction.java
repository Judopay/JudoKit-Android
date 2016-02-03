package com.judopay.model;

import com.judopay.api.Request;

import java.math.BigDecimal;

public class VoidTransaction extends Request {

    private String yourPaymentReference;
    private String receiptId;
    private BigDecimal amount;

    public VoidTransaction(String yourPaymentReference, String receiptId, BigDecimal amount) {
        this.yourPaymentReference = yourPaymentReference;
        this.receiptId = receiptId;
        this.amount = amount;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}

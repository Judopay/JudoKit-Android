package com.judopay.model;

import java.math.BigDecimal;

public class Collection {

    private final String receiptId;
    private final BigDecimal amount;
    private final String yourPaymentReference;

    public Collection(String receiptId, BigDecimal amount, String yourPaymentReference) {
        this.receiptId = receiptId;
        this.amount = amount;
        this.yourPaymentReference = yourPaymentReference;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

}
package com.judopay.model;

import java.math.BigDecimal;

/**
 * Represents a refund to be made for a payment transaction.
 */
public class Refund {

    private final Integer receiptId;
    private final BigDecimal amount;
    private final String yourPaymentReference;

    public Refund(int receiptId, BigDecimal amount, String yourPaymentReference) {
        this.receiptId = receiptId;
        this.amount = amount;
        this.yourPaymentReference = yourPaymentReference;
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

}
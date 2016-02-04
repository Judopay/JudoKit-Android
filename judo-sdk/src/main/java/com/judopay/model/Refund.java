package com.judopay.model;

import com.judopay.api.Transaction;

import java.math.BigDecimal;

/**
 * Represents a refund to be made for a payment transaction.
 */
public class Refund extends Transaction {

    private final Integer receiptId;
    private final BigDecimal amount;

    public Refund(int receiptId, BigDecimal amount, String yourPaymentReference) {
        super(yourPaymentReference);
        this.receiptId = receiptId;
        this.amount = amount;
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
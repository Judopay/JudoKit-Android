package com.judopay.model;

import com.judopay.api.Transaction;

import java.math.BigDecimal;

public final class VoidTransaction extends Transaction {

    private String receiptId;
    private BigDecimal amount;

    public VoidTransaction(String yourPaymentReference, String receiptId, BigDecimal amount) {
        super(yourPaymentReference);
        this.receiptId = receiptId;
        this.amount = amount;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
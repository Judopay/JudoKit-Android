package com.judopay.model;

import com.judopay.api.Transaction;

import java.math.BigDecimal;

/**
 * A Collection allows for the funds reserved in a pre-auth transaction to be collected.
 */
public class Collection extends Transaction {

    private final String receiptId;
    private final BigDecimal amount;

    public Collection(String yourPaymentReference, String receiptId, BigDecimal amount) {
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
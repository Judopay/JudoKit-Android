package com.judopay.model;

import com.judopay.api.Request;

import java.math.BigDecimal;

/**
 * Represents a refund to be made for a payment transaction.
 */
public class RefundRequest extends Request {

    private final String receiptId;
    private final BigDecimal amount;

    public RefundRequest(String receiptId, BigDecimal amount) {
        checkNotNull(receiptId);
        checkNotNull(amount);

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
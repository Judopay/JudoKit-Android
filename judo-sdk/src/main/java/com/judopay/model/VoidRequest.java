package com.judopay.model;

import com.judopay.api.Request;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public final class VoidRequest extends Request {

    private String receiptId;
    private BigDecimal amount;

    public VoidRequest(String receiptId, BigDecimal amount) {
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
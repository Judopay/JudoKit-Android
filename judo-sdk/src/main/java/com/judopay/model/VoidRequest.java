package com.judopay.model;

import com.judopay.api.Request;

import java.math.BigDecimal;

public final class VoidRequest extends Request {

    private String receiptId;
    private BigDecimal amount;

    public VoidRequest(String receiptId, BigDecimal amount) {
        super(true);
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
package com.judopay.model;

import com.judopay.api.Request;

import java.math.BigDecimal;

/**
 * A CollectionRequest allows for the funds reserved in a pre-auth transaction to be collected.
 */
@SuppressWarnings("unused")
public class CollectionRequest extends Request {

    private final String receiptId;
    private final BigDecimal amount;

    public CollectionRequest(String receiptId, BigDecimal amount) {
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
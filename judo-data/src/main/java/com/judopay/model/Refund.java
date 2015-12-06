package com.judopay.model;

public class Refund {

    private final int receiptId;
    private final float amount;
    private final String yourPaymentReference;

    public Refund(int receiptId, float amount, String yourPaymentReference) {
        this.receiptId = receiptId;
        this.amount = amount;
        this.yourPaymentReference = yourPaymentReference;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public float getAmount() {
        return amount;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

}

package com.judopay.model;

import com.judopay.api.Request;

import static com.judopay.arch.TextUtil.isEmpty;

/**
 * Represents a refund to be made for a payment transaction.
 */
@SuppressWarnings("unused")
public final class RefundRequest extends Request {

    private String receiptId;
    private String amount;

    public RefundRequest(final String receiptId, final String amount) {
        checkNotNull(receiptId);
        checkNotNull(amount);

        this.receiptId = receiptId;
        this.amount = amount;
    }

    private RefundRequest() { }

    public String getReceiptId() {
        return receiptId;
    }

    public String getAmount() {
        return amount;
    }

    public static class Builder {

        private String receiptId;
        private String amount;
        private String paymentReference;

        public Builder setReceiptId(final String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder setAmount(final String amount) {
            this.amount = amount;
            return this;
        }

        public Builder setPaymentReference(final String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public RefundRequest build() {
            checkNotNull(receiptId);
            checkNotNull(amount);

            RefundRequest refundRequest = new RefundRequest();

            refundRequest.receiptId = receiptId;
            refundRequest.amount = amount;

            if (!isEmpty(paymentReference)) {
                refundRequest.yourPaymentReference = paymentReference;
            }

            return refundRequest;
        }
    }
}
package com.judopay.model;

import com.judopay.api.Request;

import static com.judopay.arch.TextUtil.isEmpty;

/**
 * A request for the funds reserved in a pre-auth transaction to be collected.
 */
@SuppressWarnings("unused")
public class CollectionRequest extends Request {

    private String receiptId;
    private String amount;

    public CollectionRequest(final String receiptId, final String amount) {
        checkNotNull(receiptId);
        checkNotNull(amount);

        this.receiptId = receiptId;
        this.amount = amount;
    }

    private CollectionRequest() { }

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

        public CollectionRequest build() {
            checkNotNull(receiptId);
            checkNotNull(amount);

            CollectionRequest collectionRequest = new CollectionRequest();

            collectionRequest.receiptId = receiptId;
            collectionRequest.amount = amount;

            if (!isEmpty(paymentReference)) {
                collectionRequest.yourPaymentReference = paymentReference;
            }

            return collectionRequest;
        }
    }

}
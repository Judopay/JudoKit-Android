package com.judopay.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents the data needed to perform a payment transaction with the judo API.
 * Use the {@link PaymentRequest.Builder} for object construction.
 *
 * When creating a {@link PaymentRequest} the {@link PaymentRequest#judoId},
 * {@link PaymentRequest#amount} and {@link PaymentRequest#currency} must be provided.
 */
public final class PaymentRequest extends BasePaymentRequest {

    private Location consumerLocation;
    private Address cardAddress;
    private String cardNumber;
    private String cv2;
    private String expiryDate;
    private String startDate;
    private String issueNumber;
    private Boolean saveCardOnly;
    private String emailAddress;
    private String mobileNumber;

    private PaymentRequest() {
        super(true);
    }

    public Location getConsumerLocation() {
        return consumerLocation;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public String getCv2() {
        return cv2;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public Boolean getSaveCardOnly() {
        return saveCardOnly;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public static class Builder {

        private BigDecimal amount;
        private Location consumerLocation;
        private String currency;
        private String judoId;
        private String yourConsumerReference;
        private Address cardAddress;
        private String cardNumber;
        private String cv2;
        private String expiryDate;
        private String startDate;
        private String issueNumber;
        private Boolean saveCardOnly;
        private String emailAddress;
        private String mobileNumber;
        private Map<String, String> yourPaymentMetaData;

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            this.consumerLocation = consumerLocation;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setJudoId(String judoId) {
            this.judoId = judoId;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            this.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            this.cardAddress = cardAddress;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setCv2(String cv2) {
            this.cv2 = cv2;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            this.issueNumber = issueNumber;
            return this;
        }

        public Builder setSaveCardOnly(Boolean saveCardOnly) {
            this.saveCardOnly = saveCardOnly;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            this.yourPaymentMetaData = metaData;
            return this;
        }

        public PaymentRequest build() {
            if (this.currency == null || this.currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set");
            }

            if (this.judoId == null || this.judoId.length() == 0) {
                throw new IllegalArgumentException("judoId must be set");
            }

            if (this.amount == null) {
                throw new IllegalArgumentException("amount must be set");
            }

            PaymentRequest transaction = new PaymentRequest();

            transaction.amount = amount;
            transaction.consumerLocation = consumerLocation;
            transaction.currency = currency;
            transaction.judoId = judoId;
            transaction.yourConsumerReference = yourConsumerReference;
            transaction.cardAddress = cardAddress;
            transaction.cardNumber = cardNumber;
            transaction.cv2 = cv2;
            transaction.expiryDate = expiryDate;
            transaction.startDate = startDate;
            transaction.issueNumber = issueNumber;
            transaction.saveCardOnly = saveCardOnly;
            transaction.emailAddress = emailAddress;
            transaction.mobileNumber = mobileNumber;
            transaction.yourPaymentMetaData = yourPaymentMetaData;

            return transaction;
        }

    }
}
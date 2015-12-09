package com.judopay.model;

import com.judopay.UniqueIdentifier;
import com.judopay.api.Request;

import java.util.Map;

public class PaymentTransaction extends Request {

    private String amount;
    private Location consumerLocation;
    private String currency;
    private long judoId;
    private String yourConsumerReference;
    private Address cardAddress;
    private String cardNumber;
    private String cv2;
    private String expiryDate;
    private String startDate;
    private String issueNumber;
    private Boolean saveCardOnly;
    private Map<String, String> yourPaymentMetaData;

    private final String yourPaymentReference;

    private PaymentTransaction() {
        this.yourPaymentReference = UniqueIdentifier.generate();
    }

    public String getAmount() {
        return amount;
    }

    public Location getConsumerLocation() {
        return consumerLocation;
    }

    public String getCurrency() {
        return currency;
    }

    public long getJudoId() {
        return judoId;
    }

    public String getYourConsumerReference() {
        return yourConsumerReference;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
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

    public Map<String, String> getMetaData() {
        return yourPaymentMetaData;
    }

    public static class Builder {

        private String amount;
        private Location consumerLocation;
        private String currency;
        private long judoId;
        private String yourConsumerReference;
        private String yourPaymentReference;
        private Address cardAddress;
        private String cardNumber;
        private String cv2;
        private String expiryDate;
        private String startDate;
        private String issueNumber;
        private Boolean saveCardOnly;
        private Map<String, String> yourPaymentMetaData;

        public Builder setAmount(String amount) {
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

        public Builder setJudoId(long judoId) {
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

        public Builder setMetaData(Map<String, String> metaData) {
            this.yourPaymentMetaData = metaData;
            return this;
        }

        public PaymentTransaction build() {
            if (this.currency == null || this.currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set");
            }

            if (this.judoId == 0) {
                throw new IllegalArgumentException("judoId must be set");
            }

            if (this.amount == null || this.amount.length() == 0) {
                throw new IllegalArgumentException("amount must be set");
            }

            PaymentTransaction transaction = new PaymentTransaction();

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
            transaction.yourPaymentMetaData = yourPaymentMetaData;

            return transaction;
        }

    }
}
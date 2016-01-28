package com.judopay.model;

import com.judopay.api.Request;

import java.util.Map;

/**
 * Represents the data needed to perform a register card transaction with the judo API.
 * Use the {@link RegisterTransaction.Builder} for object construction.
 *
 * When creating a {@link RegisterTransaction} the {@link RegisterTransaction#judoId}
 * must be provided.
 */
public class RegisterTransaction extends Request {

    private String judoId;
    private Location consumerLocation;
    private String yourConsumerReference;
    private Address cardAddress;
    private String cardNumber;
    private String cv2;
    private String expiryDate;
    private String startDate;
    private String emailAddress;
    private String mobileNumber;
    private String issueNumber;
    private Map<String, String> yourPaymentMetaData;

    private final String yourPaymentReference;

    private RegisterTransaction() {
        this.yourPaymentReference = UniqueIdentifier.generate();
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

    public String getJudoId() {
        return judoId;
    }

    public Location getConsumerLocation() {
        return consumerLocation;
    }

    public String getYourConsumerReference() {
        return yourConsumerReference;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCv2() {
        return cv2;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public Map<String, String> getMetaData() {
        return yourPaymentMetaData;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public static class Builder {

        private String judoId;
        private Location consumerLocation;
        private String yourConsumerReference;
        private Address cardAddress;
        private String cardNumber;
        private String cv2;
        private String expiryDate;
        private String startDate;
        private String issueNumber;
        private String emailAddress;
        private String mobileNumber;
        private Map<String, String> yourPaymentMetaData;

        public Builder setJudoId(String judoId) {
            this.judoId = judoId;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            this.consumerLocation = consumerLocation;
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

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public void setMetaData(Map<String, String> metaData) {
            this.yourPaymentMetaData = metaData;
        }

        public RegisterTransaction build() {
            if (this.judoId == null) {
                throw new IllegalArgumentException("judoId must be set");
            }

            RegisterTransaction transaction = new RegisterTransaction();

            transaction.judoId = judoId;
            transaction.consumerLocation = consumerLocation;
            transaction.yourConsumerReference = yourConsumerReference;
            transaction.cardAddress = cardAddress;
            transaction.cardNumber = cardNumber;
            transaction.cv2 = cv2;
            transaction.expiryDate = expiryDate;
            transaction.startDate = startDate;
            transaction.issueNumber = issueNumber;
            transaction.emailAddress = emailAddress;
            transaction.mobileNumber = mobileNumber;
            transaction.yourPaymentMetaData = yourPaymentMetaData;

            return transaction;
        }

    }
}
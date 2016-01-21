package com.judopay.model;

import com.judopay.api.Request;

public class RegisterTransaction extends Request {

    private String judoId;
    private Location consumerLocation;
    private String yourConsumerReference;
    private Address cardAddress;
    private String cardNumber;
    private String cv2;
    private String expiryDate;
    private String startDate;
    private String issueNumber;

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

        public RegisterTransaction build() {
            if (this.judoId == null) {
                throw new IllegalArgumentException("judoId must be set");
            }

            RegisterTransaction registerTransaction = new RegisterTransaction();

            registerTransaction.judoId = judoId;
            registerTransaction.consumerLocation = consumerLocation;
            registerTransaction.yourConsumerReference = yourConsumerReference;
            registerTransaction.cardAddress = cardAddress;
            registerTransaction.cardNumber = cardNumber;
            registerTransaction.cv2 = cv2;
            registerTransaction.expiryDate = expiryDate;
            registerTransaction.startDate = startDate;
            registerTransaction.issueNumber = issueNumber;

            return registerTransaction;
        }

    }
}
package com.judopay.model;

public class RegisterTransaction {

    private Client clientDetails;
    private Location consumerLocation;
    private String yourConsumerReference;
    private Address cardAddress;
    private String cardNumber;
    private String cv2;
    private String expiryDate;
    private String startDate;
    private String issueNumber;

    public Client getClientDetails() {
        return clientDetails;
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

        private RegisterTransaction registerTransaction;

        public Builder() {
            this.registerTransaction = new RegisterTransaction();
        }

        public Builder setClientDetails(Client clientDetails) {
            this.registerTransaction.clientDetails = clientDetails;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            this.registerTransaction.consumerLocation = consumerLocation;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            this.registerTransaction.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            this.registerTransaction.cardAddress = cardAddress;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            this.registerTransaction.cardNumber = cardNumber;
            return this;
        }

        public Builder setCv2(String cv2) {
            this.registerTransaction.cv2 = cv2;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            this.registerTransaction.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.registerTransaction.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            this.registerTransaction.issueNumber = issueNumber;
            return this;
        }

        public RegisterTransaction build() {
            return this.registerTransaction;
        }
    }
}

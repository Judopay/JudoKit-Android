package com.judopay.payment;

import com.judopay.Client;
import com.judopay.customer.Address;
import com.judopay.customer.Location;

public class Transaction {

    private String amount;
    private Client clientDetails;
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

    public String getAmount() {
        return amount;
    }

    public Client getClientDetails() {
        return clientDetails;
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

    public static class Builder {

        private Transaction transaction;

        public Builder() {
            this.transaction = new Transaction();
        }

        public Builder setAmount(String amount) {
            transaction.amount = amount;
            return this;
        }

        public Builder setClientDetails(Client clientDetails) {
            transaction.clientDetails = clientDetails;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            transaction.consumerLocation = consumerLocation;
            return this;
        }

        public Builder setCurrency(String currency) {
            transaction.currency = currency;
            return this;
        }

        public Builder setJudoId(long judoId) {
            transaction.judoId = judoId;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            transaction.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            transaction.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            transaction.cardAddress = cardAddress;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            transaction.cardNumber = cardNumber;
            return this;
        }

        public Builder setCv2(String cv2) {
            transaction.cv2 = cv2;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            transaction.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
            transaction.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            transaction.issueNumber = issueNumber;
            return this;
        }

        public Transaction build() {
            if (transaction.currency == null || transaction.currency.length() == 0) {
                throw new IllegalArgumentException("Currency must be set for Transaction");
            }

            return transaction;
        }

    }
}

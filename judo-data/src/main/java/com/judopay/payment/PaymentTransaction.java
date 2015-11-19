package com.judopay.payment;

import com.judopay.Client;
import com.judopay.customer.Address;
import com.judopay.customer.Location;

public class PaymentTransaction {

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
    private Boolean saveCardOnly;

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

    public Boolean getSaveCardOnly() {
        return saveCardOnly;
    }

    public static class Builder {

        private PaymentTransaction paymentTransaction;

        public Builder() {
            this.paymentTransaction = new PaymentTransaction();
        }

        public Builder setAmount(String amount) {
            paymentTransaction.amount = amount;
            return this;
        }

        public Builder setClientDetails(Client clientDetails) {
            paymentTransaction.clientDetails = clientDetails;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            paymentTransaction.consumerLocation = consumerLocation;
            return this;
        }

        public Builder setCurrency(String currency) {
            paymentTransaction.currency = currency;
            return this;
        }

        public Builder setJudoId(long judoId) {
            paymentTransaction.judoId = judoId;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            paymentTransaction.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            paymentTransaction.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            paymentTransaction.cardAddress = cardAddress;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            paymentTransaction.cardNumber = cardNumber;
            return this;
        }

        public Builder setCv2(String cv2) {
            paymentTransaction.cv2 = cv2;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            paymentTransaction.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
            paymentTransaction.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            paymentTransaction.issueNumber = issueNumber;
            return this;
        }

        public Builder setSaveCardOnly(Boolean saveCardOnly) {
            paymentTransaction.saveCardOnly = saveCardOnly;
            return this;
        }

        public PaymentTransaction build() {
            if (paymentTransaction.currency == null || paymentTransaction.currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set");
            }

            if (paymentTransaction.judoId == 0) {
                throw new IllegalArgumentException("judoId must be set");
            }

            if (paymentTransaction.amount == null || paymentTransaction.amount.length() == 0) {
                throw new IllegalArgumentException("amount must be set");
            }

            return paymentTransaction;
        }

    }
}

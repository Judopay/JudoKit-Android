package com.judopay.payment;

import com.judopay.Client;
import com.judopay.customer.Address;
import com.judopay.customer.Location;

import java.util.Map;

public class Payment {

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
    private Map<String, String> yourPaymentMetaData;

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

    public Map<String, String> getMetaData() {
        return yourPaymentMetaData;
    }

    public static class Builder {

        private Payment payment;

        public Builder() {
            this.payment = new Payment();
        }

        public Builder setAmount(String amount) {
            payment.amount = amount;
            return this;
        }

        public Builder setClientDetails(Client clientDetails) {
            payment.clientDetails = clientDetails;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            payment.consumerLocation = consumerLocation;
            return this;
        }

        public Builder setCurrency(String currency) {
            payment.currency = currency;
            return this;
        }

        public Builder setJudoId(long judoId) {
            payment.judoId = judoId;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            payment.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            payment.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            payment.cardAddress = cardAddress;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            payment.cardNumber = cardNumber;
            return this;
        }

        public Builder setCv2(String cv2) {
            payment.cv2 = cv2;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            payment.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
            payment.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            payment.issueNumber = issueNumber;
            return this;
        }

        public Builder setSaveCardOnly(Boolean saveCardOnly) {
            payment.saveCardOnly = saveCardOnly;
            return this;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            payment.yourPaymentMetaData = metaData;
            return this;
        }

        public Payment build() {
            if (payment.currency == null || payment.currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set");
            }

            if (payment.judoId == 0) {
                throw new IllegalArgumentException("judoId must be set");
            }

            if (payment.amount == null || payment.amount.length() == 0) {
                throw new IllegalArgumentException("amount must be set");
            }

            return payment;
        }

    }
}

package com.judopay.payment;

import com.google.gson.annotations.SerializedName;
import com.judopay.Client;
import com.judopay.customer.Address;
import com.judopay.customer.Location;

import java.util.Map;

public class TokenPayment {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private int type;

    private String amount;
    private Client clientDetails;
    private Location consumerLocation;
    private String currency;
    private long judoId;
    private String yourConsumerReference;
    private String yourPaymentReference;
    private Address cardAddress;
    private String cv2;
    private Map<String, String> yourPaymentMetaData;

    public TokenPayment() { }

    public String getEndDate() {
        return endDate;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getToken() {
        return token;
    }

    public int getType() {
        return type;
    }

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

    public String getCv2() {
        return cv2;
    }

    public Map<String, String> getYourPaymentMetaData() {
        return yourPaymentMetaData;
    }

    public static class Builder {

        private TokenPayment tokenPayment;

        public Builder() {
            this.tokenPayment = new TokenPayment();
        }

        public Builder setEndDate(String endDate) {
            this.tokenPayment.endDate = endDate;
            return this;
        }

        public Builder setLastFour(String lastFour) {
            this.tokenPayment.lastFour = lastFour;
            return this;
        }

        public Builder setToken(String token) {
            this.tokenPayment.token = token;
            return this;
        }

        public Builder setType(int type) {
            this.tokenPayment.type = type;
            return this;
        }

        public Builder setAmount(String amount) {
            this.tokenPayment.amount = amount;
            return this;
        }

        public Builder setClientDetails(Client clientDetails) {
            this.tokenPayment.clientDetails = clientDetails;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            this.tokenPayment.consumerLocation = consumerLocation;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.tokenPayment.currency = currency;
            return this;
        }

        public Builder setJudoId(long judoId) {
            this.tokenPayment.judoId = judoId;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            this.tokenPayment.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            this.tokenPayment.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            this.tokenPayment.cardAddress = cardAddress;
            return this;
        }

        public Builder setCv2(String cv2) {
            this.tokenPayment.cv2 = cv2;
            return this;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            this.tokenPayment.yourPaymentMetaData = metaData;
            return this;
        }

        public TokenPayment build() {
            if (tokenPayment.currency == null || tokenPayment.currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set for TokenPayment");
            }

            return tokenPayment;
        }
    }

}
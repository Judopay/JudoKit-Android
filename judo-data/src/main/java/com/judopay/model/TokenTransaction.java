package com.judopay.model;

import com.google.gson.annotations.SerializedName;
import com.judopay.arch.api.Request;

import java.util.Map;

public class TokenTransaction extends Request {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private int type;

    private String amount;
    private Location consumerLocation;
    private String currency;
    private long judoId;
    private String yourConsumerReference;
    private String yourPaymentReference;
    private Address cardAddress;
    private String cv2;
    private Map<String, String> yourPaymentMetaData;

    public TokenTransaction() { }

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

        private TokenTransaction tokenTransaction;

        public Builder() {
            this.tokenTransaction = new TokenTransaction();
        }

        public Builder setEndDate(String endDate) {
            this.tokenTransaction.endDate = endDate;
            return this;
        }

        public Builder setLastFour(String lastFour) {
            this.tokenTransaction.lastFour = lastFour;
            return this;
        }

        public Builder setToken(String token) {
            this.tokenTransaction.token = token;
            return this;
        }

        public Builder setType(int type) {
            this.tokenTransaction.type = type;
            return this;
        }

        public Builder setAmount(String amount) {
            this.tokenTransaction.amount = amount;
            return this;
        }

        public Builder setConsumerLocation(Location consumerLocation) {
            this.tokenTransaction.consumerLocation = consumerLocation;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.tokenTransaction.currency = currency;
            return this;
        }

        public Builder setJudoId(long judoId) {
            this.tokenTransaction.judoId = judoId;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            this.tokenTransaction.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            this.tokenTransaction.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            this.tokenTransaction.cardAddress = cardAddress;
            return this;
        }

        public Builder setCv2(String cv2) {
            this.tokenTransaction.cv2 = cv2;
            return this;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            this.tokenTransaction.yourPaymentMetaData = metaData;
            return this;
        }

        public TokenTransaction build() {
            if (tokenTransaction.currency == null || tokenTransaction.currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set for TokenTransaction");
            }

            return tokenTransaction;
        }
    }

}
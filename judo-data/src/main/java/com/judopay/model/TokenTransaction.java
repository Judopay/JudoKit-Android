package com.judopay.model;

import com.google.gson.annotations.SerializedName;
import com.judopay.api.Request;

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
    private Long judoId;
    private String yourConsumerReference;
    private String yourPaymentReference;
    private Address cardAddress;
    private String cv2;
    private Map<String, String> yourPaymentMetaData;

    private TokenTransaction() { }

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

        private String endDate;
        private String lastFour;
        private String token;
        private int type;
        private String amount;
        private Location consumerLocation;
        private String currency;
        private Long judoId;
        private String yourConsumerReference;
        private String yourPaymentReference;
        private Address cardAddress;
        private String cv2;
        private Map<String, String> yourPaymentMetaData;

        public Builder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setLastFour(String lastFour) {
            this.lastFour = lastFour;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

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

        public Builder setJudoId(Long judoId) {
            this.judoId = judoId;
            return this;
        }

        public Builder setYourConsumerReference(String yourConsumerReference) {
            this.yourConsumerReference = yourConsumerReference;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            this.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            this.cardAddress = cardAddress;
            return this;
        }

        public Builder setCv2(String cv2) {
            this.cv2 = cv2;
            return this;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            this.yourPaymentMetaData = metaData;
            return this;
        }

        public TokenTransaction build() {
            if (currency == null || currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set for TokenTransaction");
            }

            if (this.judoId == null) {
                throw new IllegalArgumentException("judoId must be set");
            }

            if (this.amount == null || this.amount.length() == 0) {
                throw new IllegalArgumentException("amount must be set");
            }

            TokenTransaction transaction = new TokenTransaction();

            transaction.judoId = judoId;
            transaction.amount = amount;
            transaction.currency = currency;
            transaction.cardAddress = cardAddress;
            transaction.consumerLocation = consumerLocation;
            transaction.cv2 = cv2;
            transaction.token = token;
            transaction.lastFour = lastFour;
            transaction.type = type;
            transaction.endDate = endDate;
            transaction.yourPaymentMetaData = yourPaymentMetaData;
            transaction.yourConsumerReference = yourConsumerReference;
            transaction.yourPaymentReference = yourPaymentReference;

            return transaction;
        }
    }

}
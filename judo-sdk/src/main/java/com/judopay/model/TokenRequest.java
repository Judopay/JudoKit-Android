package com.judopay.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents the data needed to perform a token transaction with the judo API.
 * Use the {@link TokenRequest.Builder} for object construction.
 *
 * When creating a {@link TokenRequest} the {@link TokenRequest#judoId},
 * {@link TokenRequest#amount} and {@link TokenRequest#currency} must be provided.
 */
public final class TokenRequest extends BasePaymentRequest {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private int type;

    private Location consumerLocation;
    private Address cardAddress;
    private String cv2;
    private String emailAddress;
    private String mobileNumber;

    public TokenRequest() {
        super(true);
    }

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

    public Location getConsumerLocation() {
        return consumerLocation;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public String getCv2() {
        return cv2;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public static class Builder {

        private String endDate;
        private String lastFour;
        private String token;
        private int type;
        private BigDecimal amount;
        private Location consumerLocation;
        private String currency;
        private String judoId;
        private String yourConsumerReference;
        private Address cardAddress;
        private String cv2;
        private String emailAddress;
        private String mobileNumber;
        private Map<String, String> yourPaymentMetaData;

        public Builder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setLastFour(String lastFour) {
            this.lastFour = lastFour;
            return this;
        }

        public Builder setToken(CardToken token) {
            this.token = token.getToken();
            this.endDate = token.getEndDate();
            this.lastFour = token.getLastFour();
            this.type = token.getType();

            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
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

        public Builder setJudoId(String judoId) {
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

        public Builder setCv2(String cv2) {
            this.cv2 = cv2;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            this.yourPaymentMetaData = metaData;
            return this;
        }

        public TokenRequest build() {
            if (currency == null || currency.length() == 0) {
                throw new IllegalArgumentException("currency must be set for TokenRequest");
            }

            if (this.judoId == null) {
                throw new IllegalArgumentException("judoId must be set");
            }

            if (this.amount == null) {
                throw new IllegalArgumentException("amount must be set");
            }

            TokenRequest transaction = new TokenRequest();

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
            transaction.emailAddress = emailAddress;
            transaction.mobileNumber = mobileNumber;
            transaction.yourPaymentMetaData = yourPaymentMetaData;
            transaction.yourConsumerReference = yourConsumerReference;

            return transaction;
        }
    }

}
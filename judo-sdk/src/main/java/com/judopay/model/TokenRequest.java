package com.judopay.model;

import com.google.gson.annotations.SerializedName;
import com.judopay.api.Request;

import java.util.Map;

/**
 * Represents the data needed to perform a token transaction with the judo API.
 * Use the {@link TokenRequest.Builder} for object construction.
 *
 * When creating a {@link TokenRequest} the {@link TokenRequest#judoId},
 * {@link TokenRequest#amount} and {@link TokenRequest#currency} must be provided.
 */
public final class TokenRequest extends Request {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private int type;

    private String judoId;
    private String amount;
    private Location consumerLocation;
    private String currency;
    private String yourConsumerReference;
    private Address cardAddress;
    private String cv2;
    private String emailAddress;
    private String mobileNumber;
    private Map<String, String> yourPaymentMetaData;

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

    public String getAmount() {
        return amount;
    }

    public Location getConsumerLocation() {
        return consumerLocation;
    }

    public String getCurrency() {
        return currency;
    }

    public String getJudoId() {
        return judoId;
    }

    public String getYourConsumerReference() {
        return yourConsumerReference;
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
        private String judoId;
        private String yourConsumerReference;
        private Address cardAddress;
        private String securityCode;
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

        public Builder setSecurityCode(String securityCode) {
            this.securityCode = securityCode;
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

            if (this.amount == null || this.amount.length() == 0) {
                throw new IllegalArgumentException("amount must be set");
            }

            TokenRequest transaction = new TokenRequest();

            transaction.judoId = judoId;
            transaction.amount = amount;
            transaction.currency = currency;
            transaction.cardAddress = cardAddress;
            transaction.consumerLocation = consumerLocation;
            transaction.cv2 = securityCode;
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
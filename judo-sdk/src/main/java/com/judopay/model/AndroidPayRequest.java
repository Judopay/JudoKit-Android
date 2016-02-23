package com.judopay.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class AndroidPayRequest {

    private final BigDecimal amount;
    private final String currency;
    private final String encryptedMessage;
    private final String ephemeralPublicKey;
    private final String tag;

    private AndroidPayRequest(BigDecimal amount, String currency, String encryptedMessage, String ephemeralPublicKey, String tag) {
        this.amount = amount;
        this.currency = currency;
        this.encryptedMessage = encryptedMessage;
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.tag = tag;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public String getEphemeralPublicKey() {
        return ephemeralPublicKey;
    }

    public String getTag() {
        return tag;
    }

    public static class Builder {

        private BigDecimal amount;
        private String currency;
        private String encryptedMessage;
        private String ephemeralPublicKey;
        private String tag;

        public Builder setPaymentMethodToken(String paymentMethodToken) {
            try {
                JSONObject json = new JSONObject(paymentMethodToken);
                setEncryptedMessage(json.getString("encryptedMessage"))
                        .setEphemeralPublicKey(json.getString("ephemeralPublicKey"))
                        .setTag(json.getString("tag"));
            } catch (JSONException e) {
                throw new IllegalArgumentException("paymentMethodToken must be a valid JSON object");
            }
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setEncryptedMessage(String encryptedMessage) {
            this.encryptedMessage = encryptedMessage;
            return this;
        }

        public Builder setEphemeralPublicKey(String ephemeralPublicKey) {
            this.ephemeralPublicKey = ephemeralPublicKey;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public AndroidPayRequest build() {
            return new AndroidPayRequest(amount, currency, encryptedMessage, ephemeralPublicKey, tag);
        }
    }

}
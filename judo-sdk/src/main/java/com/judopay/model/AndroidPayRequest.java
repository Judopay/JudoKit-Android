package com.judopay.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidPayRequest {

    private final int instrumentType;
    private final String instrumentDetails;
    private final String encryptedMessage;
    private final String ephemeralPublicKey;
    private final String tag;

    private AndroidPayRequest(int instrumentType, String instrumentDetails, String encryptedMessage, String ephemeralPublicKey, String tag) {
        this.instrumentType = instrumentType;
        this.instrumentDetails = instrumentDetails;
        this.encryptedMessage = encryptedMessage;
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.tag = tag;
    }

    public int getInstrumentType() {
        return instrumentType;
    }

    public String getInstrumentDetails() {
        return instrumentDetails;
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

        private int instrumentType;
        private String instrumentDetails;
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

        public Builder setInstrumentType(int instrumentType) {
            this.instrumentType = instrumentType;
            return this;
        }

        public Builder setInstrumentDetails(String instrumentDetails) {
            this.instrumentDetails = instrumentDetails;
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
            return new AndroidPayRequest(instrumentType, instrumentDetails, encryptedMessage, ephemeralPublicKey, tag);
        }
    }

}
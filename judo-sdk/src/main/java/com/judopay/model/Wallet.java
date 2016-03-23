package com.judopay.model;

import org.json.JSONException;
import org.json.JSONObject;

import static com.judopay.arch.Preconditions.checkNotEmpty;
import static com.judopay.arch.Preconditions.checkNotNull;

public class Wallet {

    private final String encryptedMessage;
    private final String ephemeralPublicKey;
    private final String tag;
    private final String publicKey;
    private final Integer environment;

    public Wallet(String encryptedMessage, String ephemeralPublicKey, String tag, String publicKey, Integer environment) {
        this.encryptedMessage = encryptedMessage;
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.tag = tag;
        this.publicKey = publicKey;
        this.environment = environment;
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

    public String getPublicKey() {
        return publicKey;
    }

    public Integer getEnvironment() {
        return environment;
    }

    public static class Builder {

        private String encryptedMessage;
        private String ephemeralPublicKey;
        private String tag;
        private String publicKey;
        private Integer environment;

        /**
         * Sets the encrypted payload JSON on the {@link AndroidPayRequest} instance, this is equivalent
         * to calling {@link #setEncryptedMessage(String)}, {@link #setTag(String)} and {@link #setEphemeralPublicKey(String)}.
         *
         * @param paymentMethodTokenJson the payment method JSON received in the Android Pay Full Wallet response
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setPaymentMethodToken(String paymentMethodTokenJson) {
            try {
                JSONObject json = new JSONObject(paymentMethodTokenJson);
                setEncryptedMessage(json.getString("encryptedMessage"))
                        .setEphemeralPublicKey(json.getString("ephemeralPublicKey"))
                        .setTag(json.getString("tag"));
            } catch (JSONException e) {
                throw new IllegalArgumentException("paymentMethodToken must be a valid JSON object");
            }
            return this;
        }

        /**
         * @param encryptedMessage the encrypted message, this is required for an Android Pay transaction.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setEncryptedMessage(String encryptedMessage) {
            this.encryptedMessage = encryptedMessage;
            return this;
        }

        /**
         * @param ephemeralPublicKey the Android Pay ephemeral public key associated with the private key to encrypt the message,
         *                           this is required for an Android Pay transaction.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setEphemeralPublicKey(String ephemeralPublicKey) {
            this.ephemeralPublicKey = ephemeralPublicKey;
            return this;
        }

        /**
         * @param tag the MAC of the {@link #encryptedMessage}, this is required for an Android Pay transaction.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * @param publicKey the public key used for encrypting the Android Pay payment token, this is required for an Android Pay transaction.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        /**
         * @param environment the environment used when performing the Android Pay payment
         *                    see ENVIRONMENT_TEST and ENVIRONMENT_PRODUCTION in Google Play
         *                    Services Wallet class com.google.android.gms.wallet.WalletConstants,
         *                    this is required for an Android Pay transaction.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setEnvironment(Integer environment) {
            this.environment = environment;
            return this;
        }

        public Wallet build() {
            checkNotEmpty(encryptedMessage);
            checkNotEmpty(ephemeralPublicKey);
            checkNotEmpty(tag);
            checkNotEmpty(publicKey);
            checkNotNull(environment);

            return new Wallet(encryptedMessage, ephemeralPublicKey, tag, publicKey, environment);
        }
    }

}
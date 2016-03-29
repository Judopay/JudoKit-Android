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
    private final String instrumentDetails;
    private final String instrumentType;
    private final String merchantTransactionId;
    private final Integer environment;
    private final String version;

    public Wallet(String encryptedMessage,
                  String ephemeralPublicKey,
                  String tag,
                  String publicKey,
                  String instrumentDetails,
                  String instrumentType,
                  String merchantTransactionId,
                  Integer environment, String version) {
        this.encryptedMessage = encryptedMessage;
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.tag = tag;
        this.publicKey = publicKey;
        this.instrumentDetails = instrumentDetails;
        this.instrumentType = instrumentType;
        this.merchantTransactionId = merchantTransactionId;
        this.environment = environment;
        this.version = version;
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

    public String getInstrumentDetails() {
        return instrumentDetails;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public Integer getEnvironment() {
        return environment;
    }

    public String getMerchantTransactionId() {
        return merchantTransactionId;
    }

    public String getVersion() {
        return version;
    }

    public static class Builder {

        private String encryptedMessage;
        private String ephemeralPublicKey;
        private String tag;
        private String publicKey;
        private String instrumentDetails;
        private String instrumentType;
        private String merchantTransactionId;
        private Integer environment;
        private String version;

        /**
         * Sets the encrypted payload JSON on the {@link AndroidPayRequest} instance, this is
         * equivalent to calling {@link #setEncryptedMessage(String)}, {@link #setTag(String)}
         * and {@link #setEphemeralPublicKey(String)}.
         *
         * @param paymentMethodTokenJson the payment method JSON received in the Android Pay
         *                               FullWallet
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
         * @param encryptedMessage the encrypted message
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setEncryptedMessage(String encryptedMessage) {
            this.encryptedMessage = encryptedMessage;
            return this;
        }

        /**
         * @param ephemeralPublicKey the Android Pay ephemeral public key associated with the
         *                           private key to encrypt the message.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setEphemeralPublicKey(String ephemeralPublicKey) {
            this.ephemeralPublicKey = ephemeralPublicKey;
            return this;
        }

        /**
         * @param tag the MAC of the {@link #encryptedMessage}
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * @param publicKey the public key used for encrypting the Android Pay payment token.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        /**
         * @param instrumentDetails the details of the payment method used, typically this is the
         *                          last four digits of a customer's card. This can be obtained from
         *                          by calling getInstrumentInfo().getInstrumentDetails() on the
         *                          FullWallet.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setInstrumentDetails(String instrumentDetails) {
            this.instrumentDetails = instrumentDetails;
            return this;
        }

        /**
         * @param instrumentType the type of the payment method used (i.e. VISA, MASTERCARD, AMEX).
         *                       This can be obtained from by calling
         *                       getInstrumentInfo().getInstrumentType() on the FullWallet
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setInstrumentType(String instrumentType) {
            this.instrumentType = instrumentType;
            return this;
        }

        /**
         * @param merchantTransactionId the merchant ID for the transaction. This can be obtained
         *                              from the FullWallet.getMerchantId() method.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setMerchantTransactionId(String merchantTransactionId) {
            this.merchantTransactionId = merchantTransactionId;
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

        /**
         * @param version the version of the wallet, this can be obtained from the
         *                FullWallet.getVersionCode() method.
         */
        public void setVersion(String version) {
            this.version = version;
        }

        public Wallet build() {
            checkNotEmpty(encryptedMessage);
            checkNotEmpty(ephemeralPublicKey);
            checkNotEmpty(tag);
            checkNotEmpty(publicKey);
            checkNotEmpty(instrumentDetails);
            checkNotEmpty(instrumentType);
            checkNotEmpty(merchantTransactionId);
            checkNotNull(environment);
            checkNotEmpty(version);

            return new Wallet(encryptedMessage, ephemeralPublicKey, tag, publicKey, instrumentDetails, instrumentType, merchantTransactionId, environment, version);
        }
    }

}
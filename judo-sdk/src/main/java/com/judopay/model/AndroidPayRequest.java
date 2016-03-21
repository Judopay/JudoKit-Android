package com.judopay.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

import static com.judopay.arch.Preconditions.checkNotEmpty;
import static com.judopay.arch.Preconditions.checkNotNull;

/**
 * A request for performing a payment or pre-auth with the judoNative API. This contains the Android Pay
 * encrypted payload, received when requesting the FullWallet with Google Play Services and the common
 * request fields used when performing a transaction with judo.
 */
public final class AndroidPayRequest extends BasePaymentRequest {

    private String encryptedMessage;
    private String ephemeralPublicKey;
    private String tag;
    private String publicKey;
    private Integer walletEnvironment;

    public AndroidPayRequest(String encryptedMessage, String ephemeralPublicKey, String tag, String publicKey, Integer walletEnvironment) {
        super(true);
        this.encryptedMessage = encryptedMessage;
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.tag = tag;
        this.publicKey = publicKey;
        this.walletEnvironment = walletEnvironment;
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

    public Integer getWalletEnvironment() {
        return walletEnvironment;
    }

    public static class Builder {

        private String judoId;
        private BigDecimal amount;
        private String currency;
        private String encryptedMessage;
        private String ephemeralPublicKey;
        private String tag;
        private String publicKey;
        private Integer walletEnvironment;
        private String consumerReference;
        private Map<String, String> metaData;

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
         * @param amount the amount for the transaction
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        /**
         * @param currency A currency code string as defined in the list of supported currencies in {@link Currency}
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * @param judoId the judo ID of the judo account
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setJudoId(String judoId) {
            this.judoId = judoId;
            return this;
        }

        /**
         * @param consumerReference your reference to identify the transaction
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setConsumerReference(String consumerReference) {
            this.consumerReference = consumerReference;
            return this;
        }

        /**
         * @param metaData meta data to be passed with the transaction that will be returned in the response
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setMetaData(Map<String, String> metaData) {
            this.metaData = metaData;
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
         * @param walletEnvironment the environment used when performing the Android Pay payment
         *                          see ENVIRONMENT_TEST and ENVIRONMENT_PRODUCTION in Google Play
         *                          Services Wallet class com.google.android.gms.wallet.WalletConstants,
         *                          this is required for an Android Pay transaction.
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setWalletEnvironment(Integer walletEnvironment) {
            this.walletEnvironment = walletEnvironment;
            return this;
        }

        /**
         * creates the {@link AndroidPayRequest} instance with the fields from the {@link Builder}
         *
         * @return the built {@link AndroidPayRequest} instance
         */
        public AndroidPayRequest build() {
            checkNotEmpty(judoId);
            checkNotNull(amount);
            checkNotEmpty(currency);
            checkNotEmpty(encryptedMessage);
            checkNotEmpty(ephemeralPublicKey);
            checkNotEmpty(tag);
            checkNotEmpty(publicKey);
            checkNotNull(walletEnvironment);

            AndroidPayRequest androidPayRequest = new AndroidPayRequest(encryptedMessage, ephemeralPublicKey, tag, publicKey, walletEnvironment);

            androidPayRequest.judoId = judoId;
            androidPayRequest.amount = amount;
            androidPayRequest.currency = currency;
            androidPayRequest.yourPaymentMetaData = metaData;
            androidPayRequest.yourConsumerReference = consumerReference;

            return androidPayRequest;
        }
    }

}
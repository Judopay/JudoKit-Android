package com.judopay.model;

import java.math.BigDecimal;
import java.util.Map;

import static com.judopay.arch.Preconditions.checkNotEmpty;

/**
 * A request for performing a payment or pre-auth with the judoNative API. This contains the Android Pay
 * encrypted payload, received when requesting the FullWallet with Google Play Services and the common
 * request fields used when performing a transaction with judo.
 */
public final class AndroidPayRequest extends BasePaymentRequest {

    private final Wallet wallet;

    public AndroidPayRequest(Wallet wallet) {
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public static class Builder {

        private String judoId;
        private BigDecimal amount;
        private String currency;
        private String consumerReference;
        private Map<String, String> metaData;

        private Wallet wallet;

        /**
         * @param wallet the wallet information received from Android Pay containing the encrypted payload
         * @return The Builder for creating the {@link AndroidPayRequest} instance
         */
        public Builder setWallet(Wallet wallet) {
            this.wallet = wallet;
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
         * creates the {@link AndroidPayRequest} instance with the fields from the {@link Builder}
         *
         * @return the built {@link AndroidPayRequest} instance
         */
        public AndroidPayRequest build() {
            checkNotNull(amount);
            checkNotNull(wallet);
            checkNotEmpty(judoId);
            checkNotEmpty(currency);
            checkNotEmpty(consumerReference);

            AndroidPayRequest request = new AndroidPayRequest(wallet);

            request.judoId = judoId;
            request.amount = amount;
            request.currency = currency;
            request.yourPaymentMetaData = metaData;
            request.yourConsumerReference = consumerReference;

            return request;
        }
    }

}
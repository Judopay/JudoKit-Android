package com.judopay.model;

import java.util.Map;

import static com.judopay.arch.Preconditions.checkNotEmpty;

/**
 * A request for performing a payment or pre-auth with the Judopay API using Google Pay.
 * This contains information from the Google Play Services and the common request fields used when
 * performing a transaction.
 */
@SuppressWarnings({"unused"})
public final class GooglePayRequest extends BasePaymentRequest {
    private final GooglePayWallet googlePayWallet;
    private PrimaryAccountDetails primaryAccountDetails;

    private GooglePayRequest(final GooglePayWallet googlePayWallet) {
        this.googlePayWallet = googlePayWallet;
    }

    public GooglePayWallet getWallet() {
        return googlePayWallet;
    }

    public PrimaryAccountDetails getPrimaryAccountDetails() {
        return primaryAccountDetails;
    }

    public static class Builder {
        private String amount;
        private String currency;
        private String judoId;
        private String consumerReference;
        private Map<String, String> metaData;
        private GooglePayWallet googlePayWallet;
        private PrimaryAccountDetails primaryAccountDetails;

        /**
         * @param amount the amount for the transaction
         * @return The Builder for creating the {@link GooglePayRequest} instance
         */
        public Builder setAmount(final String amount) {
            this.amount = amount;
            return this;
        }

        /**
         * @param currency a currency code string as defined in the list of supported currencies in {@link Currency}
         * @return The Builder for creating the {@link GooglePayRequest} instance
         */
        public Builder setCurrency(final String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * @param judoId the judo ID of the Judo account
         * @return The Builder for creating the {@link GooglePayRequest} instance
         */
        public Builder setJudoId(final String judoId) {
            this.judoId = judoId;
            return this;
        }

        /**
         * @param consumerReference your reference to identify the transaction
         * @return The Builder for creating the {@link GooglePayRequest} instance
         */
        public Builder setConsumerReference(final String consumerReference) {
            this.consumerReference = consumerReference;
            return this;
        }

        /**
         * @param metaData data to be passed with the transaction that will be returned in the response
         * @return The Builder for creating the {@link GooglePayRequest} instance
         */
        public Builder setMetaData(final Map<String, String> metaData) {
            this.metaData = metaData;
            return this;
        }

        /**
         * @param googlePayWallet the GooglePayWallet information from Google Pay
         * @return The Builder for creating the {@link GooglePayRequest} instance
         */
        public Builder setGooglePayWallet(final GooglePayWallet googlePayWallet) {
            this.googlePayWallet = googlePayWallet;
            return this;
        }

        /**
         * @param primaryAccountDetails the PrimaryAccountDetails information for acquirer compliance
         * @return The Builder for creating the {@link GooglePayRequest} instance
         */
        public Builder setPrimaryAccountDetails(final PrimaryAccountDetails primaryAccountDetails) {
            this.primaryAccountDetails = primaryAccountDetails;
            return this;
        }

        /**
         * Creates the {@link GooglePayRequest} instance with the fields from the {@link Builder}
         *
         * @return the built {@link GooglePayRequest} instance
         */
        public GooglePayRequest build() {
            checkNotEmpty(amount);
            checkNotEmpty(currency);
            checkNotEmpty(judoId);
            checkNotEmpty(consumerReference);
            checkNotNull(googlePayWallet);

            GooglePayRequest request = new GooglePayRequest(googlePayWallet);
            request.amount = amount;
            request.currency = currency;
            request.judoId = judoId;
            request.yourConsumerReference = consumerReference;
            request.yourPaymentMetaData = metaData;
            request.primaryAccountDetails = primaryAccountDetails;

            return request;
        }
    }
}

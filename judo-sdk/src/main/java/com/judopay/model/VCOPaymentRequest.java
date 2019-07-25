package com.judopay.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import static com.judopay.arch.Preconditions.checkNotEmpty;

/**
 * A request for performing a payment or pre-auth with the Visa Checkout API. Contains the VCO
 * encrypted key and payload received when requesting payment with Visa Checkout and the common
 * request fields used when performing a transaction with judo.
 */
public final class VCOPaymentRequest extends BasePaymentRequest {

    @SerializedName("wallet")
    private final VCOWallet vcoWallet;

    @SuppressWarnings("WeakerAccess")
    public VCOPaymentRequest(final VCOWallet vcoWallet) {
        this.vcoWallet = vcoWallet;
    }

    @SuppressWarnings("unused")
    public VCOWallet getVCOWallet() {
        return vcoWallet;
    }

    public static class Builder {

        private VCOWallet vcoWallet;
        private String amount;
        private String currency;
        private String judoId;
        private String consumerReference;
        private Map<String, String> metaData;

        /**
         * @param vcoWallet the information received from Visa Checkout containing the callId, encrypted key and payload
         * @return The Builder for creating the {@link VCOPaymentRequest} instance
         */
        public VCOPaymentRequest.Builder setVCOWallet(final VCOWallet vcoWallet) {
            this.vcoWallet = vcoWallet;
            return this;
        }

        /**
         * @param amount the amount for the transaction
         * @return The Builder for creating the {@link VCOPaymentRequest} instance
         */
        public VCOPaymentRequest.Builder setAmount(final String amount) {
            this.amount = amount;
            return this;
        }

        /**
         * @param currency A currency code string as defined in the list of supported currencies in {@link Currency}
         * @return The Builder for creating the {@link VCOPaymentRequest} instance
         */
        public VCOPaymentRequest.Builder setCurrency(final String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * @param judoId the judo ID of the judo account
         * @return The Builder for creating the {@link VCOPaymentRequest} instance
         */
        public VCOPaymentRequest.Builder setJudoId(final String judoId) {
            this.judoId = judoId;
            return this;
        }

        /**
         * @param consumerReference your reference to identify the transaction
         * @return The Builder for creating the {@link VCOPaymentRequest} instance
         */
        public VCOPaymentRequest.Builder setConsumerReference(final String consumerReference) {
            this.consumerReference = consumerReference;
            return this;
        }

        /**
         * @param metaData metadata to be passed with the transaction that will be returned in the response
         * @return The Builder for creating the {@link VCOPaymentRequest} instance
         */
        public VCOPaymentRequest.Builder setMetaData(final Map<String, String> metaData) {
            this.metaData = metaData;
            return this;
        }

        /**
         * creates the {@link VCOPaymentRequest} instance with the fields from the {@link VCOPaymentRequest.Builder}
         *
         * @return the built {@link VCOPaymentRequest} instance
         */
        public VCOPaymentRequest build() {
            checkNotNull(vcoWallet);
            checkNotEmpty(amount);
            checkNotEmpty(currency);
            checkNotEmpty(judoId);
            checkNotEmpty(consumerReference);

            VCOPaymentRequest request = new VCOPaymentRequest(vcoWallet);
            request.amount = amount;
            request.currency = currency;
            request.judoId = judoId;
            request.yourPaymentMetaData = metaData;
            request.yourConsumerReference = consumerReference;

            return request;
        }
    }
}

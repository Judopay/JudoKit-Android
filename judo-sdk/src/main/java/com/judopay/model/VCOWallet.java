package com.judopay.model;

import com.google.gson.annotations.SerializedName;

import static com.judopay.arch.Preconditions.checkNotEmpty;

public final class VCOWallet {

    @SerializedName("callid")
    private final String callId;
    private final String encryptedKey;
    private final String encryptedPaymentData;

    private VCOWallet(final String callId, final String encryptedKey, final String encryptedPaymentData) {
        this.callId = callId;
        this.encryptedKey = encryptedKey;
        this.encryptedPaymentData = encryptedPaymentData;
    }

    @SuppressWarnings("unused")
    public String getCallId() {
        return callId;
    }

    @SuppressWarnings("unused")
    public String getEncryptedKey() {
        return encryptedKey;
    }

    @SuppressWarnings("unused")
    public String getEncryptedPaymentData() {
        return encryptedPaymentData;
    }

    public static class Builder {

        private String callId;
        private String encryptedKey;
        private String encryptedPaymentData;

        /**
         * @param callId the callId from Visa Checkout's VisaPaymentSummary
         * @return The Builder for creating the {@link VCOWallet} instance
         */
        public Builder setCallId(final String callId) {
            this.callId = callId;
            return this;
        }

        /**
         * @param encKey the encKey from Visa Checkout's VisaPaymentSummary
         * @return The Builder for creating the {@link VCOWallet} instance
         */
        public Builder setEncryptedKey(final String encKey) {
            this.encryptedKey = encKey;
            return this;
        }

        /**
         * @param encPaymentData the encPaymentData from Visa Checkout's VisaPaymentSummary
         * @return The Builder for creating the {@link VCOWallet} instance
         */
        public Builder setEncryptedPaymentData(final String encPaymentData) {
            this.encryptedPaymentData = encPaymentData;
            return this;
        }

        public VCOWallet build() {
            checkNotEmpty(callId);
            checkNotEmpty(encryptedKey);
            checkNotEmpty(encryptedPaymentData);

            return new VCOWallet(callId, encryptedKey, encryptedPaymentData);
        }
    }
}

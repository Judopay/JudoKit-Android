package com.judopay.model;

import com.google.gson.annotations.SerializedName;
import com.judopay.error.JudoIdInvalidError;

import java.util.Map;

import static com.judopay.arch.TextUtil.isEmpty;
import static com.judopay.model.LuhnCheck.isValid;

/**
 * Represents the data needed to perform a token transaction with the judo API.
 * Use the {@link TokenRequest.Builder} for object construction.
 * <p>
 * When creating a {@link TokenRequest} the {@link TokenRequest#judoId},
 * {@link TokenRequest#amount} and {@link TokenRequest#currency} must be provided.
 */
@SuppressWarnings("unused")
public final class TokenRequest extends BasePaymentRequest {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private int type;

    private String cv2;
    private Address cardAddress;
    private String emailAddress;
    private String mobileNumber;
    private PrimaryAccountDetails primaryAccountDetails;

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

    public String getCv2() {
        return cv2;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public static class Builder {

        private String endDate;
        private String lastFour;
        private String token;
        private int type;
        private String amount;
        private String currency;
        private String judoId;
        private String consumerReference;
        private String paymentReference;
        private Address cardAddress;
        private String cv2;
        private String emailAddress;
        private String mobileNumber;
        private Map<String, String> yourPaymentMetaData;
        private PrimaryAccountDetails primaryAccountDetails;

        public Builder setEndDate(final String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setLastFour(final String lastFour) {
            this.lastFour = lastFour;
            return this;
        }

        public Builder setToken(final CardToken token) {
            this.token = token.getToken();
            this.endDate = token.getEndDate();
            this.lastFour = token.getLastFour();
            this.type = token.getType();

            return this;
        }

        public Builder setType(final int type) {
            this.type = type;
            return this;
        }

        public Builder setAmount(final String amount) {
            this.amount = amount;
            return this;
        }

        public Builder setCurrency(final String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setJudoId(final String judoId) {
            this.judoId = judoId;
            return this;
        }

        public Builder setConsumerReference(final String consumerReference) {
            this.consumerReference = consumerReference;
            return this;
        }

        public Builder setPaymentReference(final String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public Builder setCardAddress(final Address cardAddress) {
            this.cardAddress = cardAddress;
            return this;
        }

        public Builder setCv2(final String cv2) {
            this.cv2 = cv2;
            return this;
        }

        public Builder setEmailAddress(final String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setMobileNumber(final String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder setMetaData(final Map<String, String> metaData) {
            this.yourPaymentMetaData = metaData;
            return this;
        }

        public Builder setPrimaryAccountDetails(final PrimaryAccountDetails primaryAccountDetails) {
            this.primaryAccountDetails = primaryAccountDetails;
            return this;
        }

        public TokenRequest build() {
            if (isEmpty(judoId) || !isValid(judoId)) {
                throw new JudoIdInvalidError();
            }

            checkNotNull(currency);
            checkNotNull(amount);
            checkNotNull(consumerReference);
            checkNotNull(token);

            TokenRequest request = new TokenRequest();

            request.judoId = judoId;
            request.amount = amount;
            request.currency = currency;
            request.cardAddress = cardAddress;
            request.cv2 = cv2;
            request.token = token;
            request.lastFour = lastFour;
            request.type = type;
            request.endDate = endDate;
            request.emailAddress = emailAddress;
            request.mobileNumber = mobileNumber;
            request.yourPaymentMetaData = yourPaymentMetaData;
            request.yourConsumerReference = consumerReference;
            request.primaryAccountDetails = primaryAccountDetails;

            if (!isEmpty(paymentReference)) {
                request.yourPaymentReference = paymentReference;
            }

            return request;
        }
    }

}
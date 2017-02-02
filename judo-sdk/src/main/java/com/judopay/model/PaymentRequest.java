package com.judopay.model;

import com.judopay.error.JudoIdInvalidError;

import java.util.Map;

import static com.judopay.arch.TextUtil.isEmpty;
import static com.judopay.model.LuhnCheck.isValid;

/**
 * Represents the data needed to perform a register card transaction with the judo API.
 * Use the {@link PaymentRequest.Builder} for object construction.
 * When creating a {@link PaymentRequest} the {@link PaymentRequest#judoId},
 * {@link PaymentRequest#amount} and {@link PaymentRequest#currency} must be provided.
 */
@SuppressWarnings("unused")
public final class PaymentRequest extends BasePaymentRequest {

    private Address cardAddress;
    private String cardNumber;
    private String cv2;
    private String expiryDate;
    private String startDate;
    private String issueNumber;
    private Boolean saveCardOnly;
    private String emailAddress;
    private String mobileNumber;

    public Address getCardAddress() {
        return cardAddress;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public String getCv2() {
        return cv2;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public Boolean getSaveCardOnly() {
        return saveCardOnly;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Map<String, String> getMetaData() {
        return yourPaymentMetaData;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        private String amount;
        private String currency;
        private String judoId;
        private String consumerReference;
        private String paymentReference;
        private Address cardAddress;
        private String cardNumber;
        private String cv2;
        private String expiryDate;
        private String startDate;
        private String issueNumber;
        private Boolean saveCardOnly;
        private String emailAddress;
        private String mobileNumber;
        private Map<String, String> yourPaymentMetaData;

        public Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder setCurrency(@Currency.Type String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setJudoId(String judoId) {
            this.judoId = judoId;
            return this;
        }

        public Builder setConsumerReference(String consumerReference) {
            this.consumerReference = consumerReference;
            return this;
        }

        public Builder setPaymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            this.cardAddress = cardAddress;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setCv2(String cv2) {
            this.cv2 = cv2;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            this.issueNumber = issueNumber;
            return this;
        }

        public Builder setSaveCardOnly(Boolean saveCardOnly) {
            this.saveCardOnly = saveCardOnly;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder setMetaData(Map<String, String> metaData) {
            this.yourPaymentMetaData = metaData;
            return this;
        }

        public PaymentRequest build() {
            if (isEmpty(judoId) || !isValid(judoId)) {
                throw new JudoIdInvalidError();
            }

            checkNotNull(amount);
            checkNotNull(currency);
            checkNotNull(consumerReference);
            checkNotNull(cardNumber);
            checkNotNull(cv2);
            checkNotNull(expiryDate);

            PaymentRequest request = new PaymentRequest();

            request.amount = amount;
            request.currency = currency;
            request.judoId = judoId;
            request.yourConsumerReference = consumerReference;

            if(!isEmpty(paymentReference)) {
                request.yourPaymentReference = paymentReference;
            }

            request.cardAddress = cardAddress;
            request.cardNumber = cardNumber;
            request.cv2 = cv2;
            request.expiryDate = expiryDate;
            request.startDate = startDate;
            request.issueNumber = issueNumber;
            request.saveCardOnly = saveCardOnly;
            request.emailAddress = emailAddress;
            request.mobileNumber = mobileNumber;
            request.yourPaymentMetaData = yourPaymentMetaData;

            return request;
        }

    }
}

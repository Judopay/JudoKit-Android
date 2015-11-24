package com.judopay.payment.form;

import com.judopay.customer.Country;

public class PaymentForm {

    private int cardType;
    private boolean addressRequired;
    private boolean amexSupported;
    private boolean maestroSupported;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String startDate;
    private String issueNumber;
    private Country country;
    private String postcode;
    private boolean tokenCard;

    public int getCardType() {
        return cardType;
    }

    public boolean isAddressRequired() {
        return addressRequired;
    }

    public boolean isAmexSupported() {
        return amexSupported;
    }

    public boolean isMaestroSupported() {
        return maestroSupported;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public Country getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }

    public boolean isTokenCard() {
        return tokenCard;
    }

    public static class Builder {

        private PaymentForm paymentForm;

        public Builder() {
            this.paymentForm = new PaymentForm();
        }

        public Builder setAddressRequired(boolean addressRequired) {
            paymentForm.addressRequired = addressRequired;
            return this;
        }

        public Builder setAmexSupported(boolean amexSupported) {
            paymentForm.amexSupported = amexSupported;
            return this;
        }

        public Builder setMaestroSupported(boolean maestroSupported) {
            paymentForm.maestroSupported = maestroSupported;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            paymentForm.cardNumber = cardNumber;
            return this;
        }

        public Builder setCardType(int cardType) {
            paymentForm.cardType = cardType;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            paymentForm.expiryDate = expiryDate;
            return this;
        }

        public Builder setCvv(String cvv) {
            paymentForm.cvv = cvv;
            return this;
        }

        public Builder setStartDate(String startDate) {
            paymentForm.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            paymentForm.issueNumber = issueNumber;
            return this;
        }

        public Builder setCountry(Country country) {
            paymentForm.country = country;
            return this;
        }

        public Builder setPostcode(String postcode) {
            paymentForm.postcode = postcode;
            return this;
        }

        public Builder setTokenCard(boolean tokenCard) {
            paymentForm.tokenCard = tokenCard;
            return this;
        }

        public PaymentForm build() {
            return paymentForm;
        }
    }

}
package com.judopay;

import android.support.annotation.StringRes;

import com.judopay.model.CardDate;
import com.judopay.model.CardNetwork;

@Deprecated
public class PaymentFormValidation {

    private CardNumberValidation cardNumberValidation;
    private CountryAndPostcodeValidation countryAndPostcodeValidation;
    private StartDateAndIssueNumberValidation startDateAndIssueNumberValidation;

    private int cardType;

    private int expiryDateError;
    private boolean showExpiryDateError;
    private boolean expiryDateEntryComplete;

    private boolean securityCodeValid;
    private int securityCodeLength;
    private int securityCodeHint;

    private boolean paymentButtonEnabled;

    public CardNumberValidation getCardNumberValidation() {
        return cardNumberValidation;
    }

    public CountryAndPostcodeValidation getCountryAndPostcodeValidation() {
        return countryAndPostcodeValidation;
    }

    public StartDateAndIssueNumberValidation getStartDateAndIssueNumberState() {
        return startDateAndIssueNumberValidation;
    }

    public boolean isShowExpiryDateError() {
        return showExpiryDateError;
    }

    public boolean isExpiryDateEntryComplete() {
        return expiryDateEntryComplete;
    }

    public boolean isSecurityCodeValid() {
        return securityCodeValid;
    }

    public boolean isPaymentButtonEnabled() {
        return paymentButtonEnabled;
    }

    public int getCardType() {
        return cardType;
    }

    @StringRes
    public int getExpiryDateError() {
        return expiryDateError;
    }

    @StringRes
    public int getSecurityCodeHint() {
        return securityCodeHint;
    }

    public int getSecurityCodeLength() {
        return securityCodeLength;
    }

    public static class Builder {

        private final PaymentFormValidation paymentFormValidation;

        public Builder() {
            this.paymentFormValidation = new PaymentFormValidation();
        }

        public PaymentFormValidation build() {
            return paymentFormValidation;
        }

        public int getCvvHint(int cardType) {
            return CardNetwork.AMEX == cardType ? R.string.amex_security_code_hint : R.string.security_code_hint;
        }

        public PaymentFormValidation build(PaymentForm paymentForm) {
            int cardType = paymentForm.getCardType() > 0 ? paymentForm.getCardType() :
                    CardNetwork.fromCardNumber(paymentForm.getCardNumber());

            boolean maestroCardType = cardType == CardNetwork.MAESTRO;

            CardNumberValidation cardNumberValidation = new CardNumberValidation(paymentForm.getCardNumber(),
                    cardType,
                    paymentForm.isTokenCard(),
                    paymentForm.isMaestroSupported(),
                    paymentForm.isAmexSupported());

            StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(paymentForm, cardType);

            boolean maestroValid = paymentForm.isTokenCard() || !maestroCardType ||
                    (startDateAndIssueNumberValidation.isStartDateEntryComplete() && !startDateAndIssueNumberValidation.isShowStartDateError())
                            && startDateAndIssueNumberValidation.isIssueNumberValid();

            boolean securityCodeValid = isSecurityCodeValid(cardType, paymentForm.getSecurityCode());

            boolean expiryDateValid = paymentForm.isTokenCard() || isExpiryDateValid(paymentForm.getExpiryDate());

            CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(paymentForm,
                    cardNumberValidation.isValid(), securityCodeValid, expiryDateValid, maestroValid);

            Builder builder = new Builder()
                    .setCardNumberValidation(cardNumberValidation)
                    .setSecurityCodeHint(getCvvHint(cardType))
                    .setCardType(cardType)
                    .setCountryAndPostcodeValidation(countryAndPostcodeValidation)
                    .setStartDateAndIssueNumberValidation(startDateAndIssueNumberValidation)
                    .setSecurityCodeValid(securityCodeValid)
                    .setSecurityCodeLength(cardType == CardNetwork.AMEX ? 4 : 3);

            setExpiryDate(builder, expiryDateValid, paymentForm);

            builder.setPaymentButtonEnabled((paymentForm.isTokenCard() || cardNumberValidation.isValid()) && securityCodeValid && expiryDateValid && maestroValid
                    && (!paymentForm.isAddressRequired() || paymentForm.isTokenCard() || countryAndPostcodeValidation.isPostcodeEntryComplete()));

            return builder.build();
        }

        private void setExpiryDate(Builder builder, boolean expiryDateValid, PaymentForm paymentForm) {
            if (!expiryDateValid) {
                builder.setExpiryDateError(R.string.check_expiry_date);
            }

            boolean expiryLengthValid = paymentForm.getExpiryDate().length() == 5;

            builder.setExpiryDateEntryComplete(expiryLengthValid)
                    .setShowExpiryDateError(!expiryDateValid && expiryLengthValid);
        }

        private boolean isExpiryDateValid(String expiryDate) {
            CardDate cardDate = new CardDate(expiryDate);
            return cardDate.isAfterToday() && cardDate.isInsideAllowedDateRange();
        }

        private boolean isSecurityCodeValid(int cardType, String securityCode) {
            try {
                int securityCodeNumber = Integer.parseInt(securityCode);

                if (CardNetwork.AMEX == cardType) {
                    return securityCode.length() == 4 && securityCodeNumber >= 0 && securityCodeNumber < 10000;
                } else {
                    return securityCode.length() == 3 && securityCodeNumber > 0 && securityCodeNumber < 1000;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public Builder setCardNumberValidation(CardNumberValidation cardNumberValidation) {
            paymentFormValidation.cardNumberValidation = cardNumberValidation;
            return this;
        }

        public Builder setCountryAndPostcodeValidation(CountryAndPostcodeValidation countryAndPostcodeValidation) {
            paymentFormValidation.countryAndPostcodeValidation = countryAndPostcodeValidation;
            return this;
        }

        public Builder setStartDateAndIssueNumberValidation(StartDateAndIssueNumberValidation startDateAndIssueNumberValidation) {
            paymentFormValidation.startDateAndIssueNumberValidation = startDateAndIssueNumberValidation;
            return this;
        }

        public Builder setSecurityCodeHint(int securityCode) {
            paymentFormValidation.securityCodeHint = securityCode;
            return this;
        }

        public Builder setSecurityCodeLength(int securityCodeLength) {
            paymentFormValidation.securityCodeLength = securityCodeLength;
            return this;
        }

        public Builder setCardType(int cardType) {
            paymentFormValidation.cardType = cardType;
            return this;
        }

        public Builder setSecurityCodeValid(boolean securityCodeValid) {
            paymentFormValidation.securityCodeValid = securityCodeValid;
            return this;
        }

        public Builder setPaymentButtonEnabled(boolean paymentButtonEnabled) {
            paymentFormValidation.paymentButtonEnabled = paymentButtonEnabled;
            return this;
        }

        public Builder setExpiryDateError(int expiryDateError) {
            paymentFormValidation.expiryDateError = expiryDateError;
            return this;
        }

        public Builder setShowExpiryDateError(boolean showExpiryDateError) {
            paymentFormValidation.showExpiryDateError = showExpiryDateError;
            return this;
        }

        public Builder setExpiryDateEntryComplete(boolean expiryDateEntryComplete) {
            paymentFormValidation.expiryDateEntryComplete = expiryDateEntryComplete;
            return this;
        }

    }

}
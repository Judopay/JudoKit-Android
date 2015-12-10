package com.judopay;

import android.support.annotation.StringRes;

import com.judopay.model.CardType;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

public class PaymentFormValidation {

    private CardNumberValidation cardNumberValidation;
    private CountryAndPostcodeValidation countryAndPostcodeValidation;
    private StartDateAndIssueNumberValidation startDateAndIssueNumberValidation;

    private int cardType;
    private boolean showAmexCvvView;

    private int expiryDateError;
    private boolean showExpiryDateError;
    private boolean expiryDateEntryComplete;

    private boolean cvvValid;
    private int cvvLength;
    private int cvvHint;
    private int cvvError;
    private int cvvLabel;

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

    public boolean isCvvValid() {
        return cvvValid;
    }

    public boolean isPaymentButtonEnabled() {
        return paymentButtonEnabled;
    }

    public int getCardType() {
        return cardType;
    }

    public boolean isShowAmexCvvView() {
        return showAmexCvvView;
    }

    @StringRes
    public int getExpiryDateError() {
        return expiryDateError;
    }

    @StringRes
    public int getCvvError() {
        return cvvError;
    }

    @StringRes
    public int getCvvHint() {
        return cvvHint;
    }

    public int getCvvLength() {
        return cvvLength;
    }

    @StringRes
    public int getCvvLabel() {
        return cvvLabel;
    }

    public static class Builder {

        private PaymentFormValidation paymentFormValidation;

        public Builder() {
            this.paymentFormValidation = new PaymentFormValidation();
        }

        public PaymentFormValidation build() {
            return paymentFormValidation;
        }

        public int getCvvHint(int cardType) {
            return CardType.AMEX == cardType ? R.string.amex_cvv_hint : R.string.cvv_hint;
        }

        public PaymentFormValidation build(PaymentForm paymentForm) {
            int cardType = paymentForm.getCardType() > 0 ? paymentForm.getCardType() :
                    CardType.matchCardNumber(paymentForm.getCardNumber());

            boolean maestroCardType = cardType == CardType.MAESTRO;

            CardNumberValidation cardNumberValidation = new CardNumberValidation(paymentForm.getCardNumber(),
                    cardType,
                    paymentForm.isTokenCard(),
                    paymentForm.isMaestroSupported(),
                    paymentForm.isAmexSupported());

            StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(paymentForm, cardType);

            boolean maestroValid = !maestroCardType ||
                    (startDateAndIssueNumberValidation.isStartDateEntryComplete() && !startDateAndIssueNumberValidation.isShowStartDateError())
                            && startDateAndIssueNumberValidation.isIssueNumberValid();

            boolean cvvValid = isCvvValid(cardType, paymentForm.getCvv());

            boolean expiryDateValid = paymentForm.isTokenCard() || isExpiryDateValid(paymentForm.getExpiryDate());

            CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(paymentForm,
                    cardNumberValidation.isValid(), cvvValid, expiryDateValid, maestroValid);

            Builder builder = new Builder()
                    .setCardNumberValidation(cardNumberValidation)
                    .setCvvHint(getCvvHint(cardType))
                    .setCardType(cardType)
                    .setCountryAndPostcodeValidation(countryAndPostcodeValidation)
                    .setStartDateAndIssueNumberValidation(startDateAndIssueNumberValidation);

            setExpiryDate(builder, expiryDateValid, paymentForm);

            setCvv(paymentForm, builder, cvvValid);
            builder.setCvvLength(cardType == CardType.AMEX ? 4 : 3);

            builder.setPaymentButtonEnabled(paymentForm.isTokenCard() || cardNumberValidation.isValid() && cvvValid && expiryDateValid && maestroValid
                    && (!paymentForm.isAddressRequired() || countryAndPostcodeValidation.isPostcodeEntryComplete() && countryAndPostcodeValidation.isCountryValid()));

            return builder.build();
        }

        private void setExpiryDate(Builder builder, boolean expiryDateValid, PaymentForm paymentForm) {
            if (!expiryDateValid) {
                builder.setExpiryDateError(R.string.error_check_date);
            }

            boolean expiryLengthValid = paymentForm.getExpiryDate().length() == 5;

            builder.setExpiryDateEntryComplete(expiryLengthValid)
                    .setShowExpiryDateError(!expiryDateValid && expiryLengthValid);
        }

        private void setCvv(PaymentForm paymentForm, Builder builder, boolean cvvValid) {
            boolean amex = CardType.matchCardNumber(paymentForm.getCardNumber()) == CardType.AMEX;

            builder.setCvvValid(cvvValid)
                    .setShowAmexCvvView(amex)
                    .setCvvLabel(amex ? R.string.amex_cvv_label : R.string.cvv_label);

            if (!cvvValid) {
                builder.setCvvError(R.string.error_generic);
            }
        }

        private boolean isExpiryDateValid(String expiryDate) {
            DateTime midnightToday = new DateTime().withTimeAtStartOfDay();

            if (!expiryDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
                return false;
            }

            int year = 2000 + Integer.parseInt(expiryDate.substring(3, 5));
            int month = Integer.parseInt(expiryDate.substring(0, 2));

            YearMonth yearMonth = new YearMonth(year, month);
            DateTime dateTime = yearMonth.toDateTime(null);

            LocalDate monthAndYear = yearMonth.toLocalDate(dateTime.dayOfMonth().getMaximumValue());

            return monthAndYear.isAfter(midnightToday.toLocalDate());
        }

        private boolean isCvvValid(int cardType, String cvv) {
            try {
                int cvvNumber = Integer.parseInt(cvv);

                if (CardType.AMEX == cardType) {
                    return cvv.length() == 4 && cvvNumber >= 0 && cvvNumber < 10000;
                } else {
                    return cvv.length() == 3 && cvvNumber > 0 && cvvNumber < 1000;
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

        public Builder setCvvHint(int cvvHint) {
            paymentFormValidation.cvvHint = cvvHint;
            return this;
        }

        public void setCvvLabel(int cvvLabel) {
            paymentFormValidation.cvvLabel = cvvLabel;
        }

        public Builder setCvvLength(int cvvLength) {
            paymentFormValidation.cvvLength = cvvLength;
            return this;
        }

        public Builder setShowAmexCvvView(boolean showAmexCvvView) {
            paymentFormValidation.showAmexCvvView = showAmexCvvView;
            return this;
        }

        public Builder setCardType(int cardType) {
            paymentFormValidation.cardType = cardType;
            return this;
        }

        public Builder setCvvValid(boolean cvvValid) {
            paymentFormValidation.cvvValid = cvvValid;
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

        public Builder setCvvError(int cvvError) {
            paymentFormValidation.cvvError = cvvError;
            return this;
        }
    }
}
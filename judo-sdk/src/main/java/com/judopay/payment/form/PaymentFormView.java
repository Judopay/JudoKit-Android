package com.judopay.payment.form;

import android.support.annotation.StringRes;

import com.judopay.R;
import com.judopay.customer.CardNumber;
import com.judopay.customer.CardType;
import com.judopay.customer.Country;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

public class PaymentFormView {

    private int cardType;
    private boolean showAmexCvvView;

    private boolean cardNumberValid;
    private boolean expiryDateValid;
    private boolean cvvValid;
    private boolean startDateValid;
    private boolean issueNumberValid;
    private boolean postcodeValid;
    private boolean issueNumberAndStartDateRequired;
    private boolean paymentButtonEnabled;
    private boolean countryAndPostcodeRequired;

    private int cvvLength;
    private int cvvHint;
    private int cardNumberError;
    private int expiryDateError;
    private int startDateError;
    private int cvvError;
    private int postcodeLabel;
    private int postcodeError;

    public boolean isCardNumberValid() {
        return cardNumberValid;
    }

    public boolean isExpiryDateValid() {
        return expiryDateValid;
    }

    public boolean isCvvValid() {
        return cvvValid;
    }

    public boolean isStartDateValid() {
        return startDateValid;
    }

    public boolean issueNumberValid() {
        return issueNumberValid;
    }

    public boolean isPostcodeValid() {
        return postcodeValid;
    }

    public boolean isIssueNumberAndStartDateRequired() {
        return issueNumberAndStartDateRequired;
    }

    public boolean isCountryAndPostcodeRequired() {
        return countryAndPostcodeRequired;
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
    public int getCardNumberError() {
        return cardNumberError;
    }

    @StringRes
    public int getExpiryDateError() {
        return expiryDateError;
    }

    @StringRes
    public int getStartDateError() {
        return startDateError;
    }

    @StringRes
    public int getCvvError() {
        return cvvError;
    }

    @StringRes
    public int getPostcodeLabel() {
        return postcodeLabel;
    }

    @StringRes
    public int getCvvHint() {
        return cvvHint;
    }

    public int getCvvLength() {
        return cvvLength;
    }

    @StringRes
    public int getPostcodeError() {
        return postcodeError;
    }

    public static class Builder {

        private PaymentFormView paymentFormView;

        public Builder() {
            this.paymentFormView = new PaymentFormView();
        }

        public PaymentFormView build() {
            return paymentFormView;
        }

        public boolean isCardNumberValid(PaymentForm paymentForm) {
            int cardType = paymentForm.getCardType();

            return CardNumber.isLuhnValid(paymentForm.getCardNumber())
                    && (((cardType != CardType.MAESTRO || paymentForm.isMaestroSupported()))
                    && (cardType != CardType.AMEX || paymentForm.isAmexSupported()));
        }

        public int getCvvHint(int cardType) {
            return CardType.AMEX == cardType ? R.string.amex_cvv_hint : R.string.cvv_hint;
        }

        public PaymentFormView build(PaymentForm paymentForm) {
            Builder builder = new Builder();

            boolean cardNumberValid = isCardNumberValid(paymentForm);
            setCardNumber(paymentForm, builder, cardNumberValid);

            boolean cvvValid = isCvvValid(paymentForm);
            setCvv(paymentForm, builder, cvvValid);
            builder.setCvvLength(paymentForm.getCardType() == CardType.AMEX ? 4 : 3);

            boolean expiryDateValid = isExpiryDateValid(paymentForm.getExpiryDate());
            setExpiryDate(paymentForm, builder, expiryDateValid);

            boolean startDateValid = isStartDateValid(paymentForm.getStartDate());
            builder.setStartDateValid(startDateValid);

            if (!startDateValid) {
                builder.setStartDateError(R.string.error_check_date);
            }

            boolean issueNumberValid = isIssueNumberValid(paymentForm.getIssueNumber());
            builder.setIssueNumberValid(issueNumberValid);

            boolean maestroCardType = paymentForm.getCardType() == CardType.MAESTRO;

            boolean postcodeValid = paymentForm.getPostcode().length() > 0;
            boolean countryValid = !paymentForm.getCountry().getDisplayName().equals(Country.OTHER);

            builder.setPaymentButtonEnabled(cardNumberValid && cvvValid && expiryDateValid && (!maestroCardType || startDateValid && issueNumberValid)
                    && (!paymentForm.isAddressRequired() || postcodeValid && countryValid));

            builder.setIssueNumberAndStartDateRequired(paymentForm.isMaestroSupported() && maestroCardType && cardNumberValid && cvvValid && expiryDateValid);

            builder.setCountryAndPostcodeRequired(paymentForm.isAddressRequired() && cardNumberValid && cvvValid && expiryDateValid);

            setPostcodeLabel(paymentForm, builder);
            builder.setPostcodeValid(postcodeValid);

            if(!postcodeValid) {
                setPostcodeError(paymentForm, builder);
            }

            return builder.build();
        }

        private void setPostcodeError(PaymentForm paymentForm, Builder builder) {
            switch (paymentForm.getCountry().getDisplayName()) {
                case Country.UNITED_KINGDOM:
                default:
                    builder.setPostcodeError(R.string.error_postcode_uk);
                    break;

                case Country.CANADA:
                    builder.setPostcodeError(R.string.error_postcode_canada);
                    break;

                case Country.UNITED_STATES:
                    builder.setPostcodeError(R.string.error_postcode_us);
            }
        }

        private void setPostcodeLabel(PaymentForm paymentForm, Builder builder) {
            switch (paymentForm.getCountry().getDisplayName()) {
                case Country.UNITED_STATES:
                    builder.setPostcodeLabel(R.string.postcode_us);
                    break;

                case Country.CANADA:
                    builder.setPostcodeLabel(R.string.postcode_canada);
                    break;

                case Country.UNITED_KINGDOM:
                default:
                    builder.setPostcodeLabel(R.string.postcode_uk);
                    break;
            }
        }

        private boolean isIssueNumberValid(String issueNumber) {
            try {
                int issueNo = Integer.parseInt(issueNumber);
                return issueNo > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private boolean isStartDateValid(String startDate) {
            if (!startDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
                return false;
            }

            DateTime midnightToday = new DateTime().withTimeAtStartOfDay();

            int year = 2000 + Integer.parseInt(startDate.substring(3, 5));
            int month = Integer.parseInt(startDate.substring(0, 2));

            LocalDate monthAndYear = new YearMonth(year, month).toLocalDate(1);

            return monthAndYear.isBefore(midnightToday.toLocalDate());
        }

        private void setExpiryDate(PaymentForm paymentForm, Builder builder, boolean expiryDateValid) {
            builder.setExpiryDateValid(expiryDateValid);

            if (!expiryDateValid) {
                builder.setExpiryDateError(R.string.error_check_date);
            }
        }

        private void setCvv(PaymentForm paymentForm, Builder builder, boolean cvvValid) {
            boolean showAmexCvvView = CardType.matchCardNumber(paymentForm.getCardNumber()) == CardType.AMEX;

            builder.setCvvValid(cvvValid)
                    .setShowAmexCvvView(showAmexCvvView);

            if (!cvvValid) {
                builder.setCvvError(R.string.error_generic);
            }
        }

        private boolean isExpiryDateValid(String expiryDate) {
            if (!expiryDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
                return false;
            }

            DateTime midnightToday = new DateTime().withTimeAtStartOfDay();

            int year = 2000 + Integer.parseInt(expiryDate.substring(3, 5));
            int month = Integer.parseInt(expiryDate.substring(0, 2));

            YearMonth yearMonth = new YearMonth(year, month);
            DateTime dateTime = yearMonth.toDateTime(null);

            LocalDate monthAndYear = yearMonth.toLocalDate(dateTime.dayOfMonth().getMaximumValue());

            return monthAndYear.isAfter(midnightToday.toLocalDate());
        }

        private boolean isCvvValid(PaymentForm paymentForm) {
            try {
                int cvv = Integer.parseInt(paymentForm.getCvv());

                if (CardType.AMEX == CardType.matchCardNumber(paymentForm.getCardNumber())) {
                    return cvv >= 0 && cvv < 10000;
                } else {
                    return cvv > 0 && cvv < 1000;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private void setCardNumber(PaymentForm paymentForm, Builder builder, boolean cardNumberValid) {
            int cvvHint = getCvvHint(paymentForm.getCardType());

            builder.setCardNumberValid(cardNumberValid)
                    .setCardType(CardType.matchCardNumber(paymentForm.getCardNumber()))
                    .setCvvHint(cvvHint);

            boolean maestroError = paymentForm.getCardType() == CardType.MAESTRO && !paymentForm.isMaestroSupported();
            boolean amexError = paymentForm.getCardType() == CardType.AMEX && !paymentForm.isAmexSupported();

            if (maestroError) {
                builder.setCardNumberError(R.string.error_maestro_not_supported);
            } else if (amexError) {
                builder.setCardNumberError(R.string.error_amex_not_supported);
            } else if (!cardNumberValid) {
                builder.setCardNumberError(R.string.error_card_number);
            }
        }

        public Builder setCvvHint(int cvvHint) {
            paymentFormView.cvvHint = cvvHint;
            return this;
        }

        public Builder setCvvLength(int cvvLength) {
            paymentFormView.cvvLength = cvvLength;
            return this;
        }

        public Builder setShowAmexCvvView(boolean showAmexCvvView) {
            paymentFormView.showAmexCvvView = showAmexCvvView;
            return this;
        }

        public Builder setCardType(int cardType) {
            paymentFormView.cardType = cardType;
            return this;
        }

        public Builder setCardNumberValid(boolean cardNumberValid) {
            paymentFormView.cardNumberValid = cardNumberValid;
            return this;
        }

        public Builder setExpiryDateValid(boolean expiryDateValid) {
            paymentFormView.expiryDateValid = expiryDateValid;
            return this;
        }

        public Builder setCvvValid(boolean cvvValid) {
            paymentFormView.cvvValid = cvvValid;
            return this;
        }

        public Builder setStartDateValid(boolean startDateValid) {
            paymentFormView.startDateValid = startDateValid;
            return this;
        }

        public Builder setIssueNumberValid(boolean issueNumberValid) {
            paymentFormView.issueNumberValid = issueNumberValid;
            return this;
        }

        public Builder setIssueNumberAndStartDateRequired(boolean issueNumberAndStartDateRequired) {
            paymentFormView.issueNumberAndStartDateRequired = issueNumberAndStartDateRequired;
            return this;
        }

        public Builder setPostcodeValid(boolean postcodeValid) {
            paymentFormView.postcodeValid = postcodeValid;
            return this;
        }

        public Builder setPaymentButtonEnabled(boolean paymentButtonEnabled) {
            paymentFormView.paymentButtonEnabled = paymentButtonEnabled;
            return this;
        }

        public Builder setCountryAndPostcodeRequired(boolean countryAndPostcodeRequired) {
            paymentFormView.countryAndPostcodeRequired = countryAndPostcodeRequired;
            return this;
        }

        public Builder setCardNumberError(int cardNumberError) {
            paymentFormView.cardNumberError = cardNumberError;
            return this;
        }

        public Builder setExpiryDateError(int expiryDateError) {
            paymentFormView.expiryDateError = expiryDateError;
            return this;
        }

        public Builder setStartDateError(int startDateError) {
            paymentFormView.startDateError = startDateError;
            return this;
        }

        public Builder setCvvError(int cvvError) {
            paymentFormView.cvvError = cvvError;
            return this;
        }

        public Builder setPostcodeLabel(int postcodeLabel) {
            paymentFormView.postcodeLabel = postcodeLabel;
            return this;
        }

        public Builder setPostcodeError(int postcodeError) {
            paymentFormView.postcodeError = postcodeError;
            return this;
        }

    }
}

package com.judopay.payment.form.address;

import com.judopay.R;
import com.judopay.customer.Country;
import com.judopay.payment.form.PaymentForm;

public class CountryAndPostcodeValidation {

    private int postcodeLabel;
    private int postcodeError;
    private boolean showPostcodeError;
    private boolean postcodeEntryComplete;

    private boolean countryValid;
    private boolean showCountryAndPostcode;
    private boolean postcodeNumeric;

    public CountryAndPostcodeValidation(PaymentForm paymentForm, boolean cardNumberValid, boolean cvvValid, boolean expiryDateValid, boolean maestroValid) {
        boolean postcodeValid = isPostcodeValid(paymentForm.getPostcode());

        this.postcodeEntryComplete = postcodeValid;
        this.showPostcodeError = !postcodeValid && paymentForm.getPostcode().length() > 0;

        this.postcodeNumeric = Country.UNITED_STATES.equals(paymentForm.getCountry().getDisplayName());
        this.countryValid = isCountryValid(paymentForm.getCountry().getDisplayName());
        this.showCountryAndPostcode = (paymentForm.isAddressRequired() && cardNumberValid && cvvValid && expiryDateValid) && (!paymentForm.isMaestroSupported() || maestroValid);
        this.postcodeLabel = getPostcodeLabel(paymentForm.getCountry());
        this.postcodeError = getPostcodeError(paymentForm.getCountry());
    }

    public boolean isShowPostcodeError() {
        return showPostcodeError;
    }

    public boolean isPostcodeEntryComplete() {
        return postcodeEntryComplete;
    }

    public boolean isCountryValid() {
        return countryValid;
    }

    public boolean isShowCountryAndPostcode() {
        return showCountryAndPostcode;
    }

    private boolean isCountryValid(String country) {
        return !country.equals(Country.OTHER);
    }

    private boolean isPostcodeValid(String postcode) {
        return postcode != null && postcode.length() > 0;
    }

    public boolean isPostcodeNumeric() {
        return postcodeNumeric;
    }

    public int getPostcodeLabel() {
        return postcodeLabel;
    }

    public int getPostcodeError() {
        return postcodeError;
    }

    private int getPostcodeError(Country country) {
        switch (country.getDisplayName()) {
            case Country.UNITED_KINGDOM:
            default:
                return R.string.error_postcode_uk;

            case Country.CANADA:
                return R.string.error_postcode_canada;

            case Country.UNITED_STATES:
                return R.string.error_postcode_us;
        }
    }

    private int getPostcodeLabel(Country country) {
        switch (country.getDisplayName()) {
            case Country.UNITED_STATES:
                return R.string.postcode_us;

            case Country.CANADA:
                return R.string.postcode_canada;

            case Country.UNITED_KINGDOM:
            default:
                return R.string.postcode_uk;
        }
    }

}
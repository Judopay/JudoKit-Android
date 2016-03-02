package com.judopay;

import com.judopay.model.Country;

public class CountryAndPostcodeValidation {

    private final int postcodeLabel;
    private final int postcodeError;
    private final boolean showPostcodeError;
    private final boolean postcodeEntryComplete;

    private final boolean showCountryAndPostcode;
    private final boolean postcodeEnabled;
    private final boolean postcodeNumeric;

    public CountryAndPostcodeValidation(PaymentForm paymentForm, boolean cardNumberValid, boolean cvvValid, boolean expiryDateValid, boolean maestroValid) {
        boolean postcodeValid = isPostcodeValid(paymentForm.getPostcode(), paymentForm.getCountry());

        this.postcodeEntryComplete = postcodeValid;
        this.showPostcodeError = !postcodeValid && paymentForm.getPostcode().length() > 0;

        this.postcodeNumeric = Country.UNITED_STATES.equals(paymentForm.getCountry().getDisplayName());
        this.postcodeEnabled = !Country.OTHER.equals(paymentForm.getCountry().getDisplayName());
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

    public boolean isShowCountryAndPostcode() {
        return showCountryAndPostcode;
    }

    private boolean isPostcodeValid(String postcode, Country country) {
        return (postcode != null && postcode.length() > 0) || Country.OTHER.equals(country.getDisplayName());
    }

    public boolean isPostcodeNumeric() {
        return postcodeNumeric;
    }

    public boolean isPostcodeEnabled() {
        return postcodeEnabled;
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
                return R.string.billing_zip_code;

            case Country.CANADA:
                return R.string.billing_postal_code;

            default:
                return R.string.billing_postcode;
        }
    }

}
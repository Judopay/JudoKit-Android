package com.judopay;

import com.judopay.model.Country;

import static android.text.TextUtils.isEmpty;

public class CountryAndPostcodeValidation {

    private final int postcodeLabel;
    private final int postcodeError;
    private final boolean showPostcodeError;
    private final boolean postcodeEntryComplete;

    private final boolean countryValid;
    private final boolean showCountryAndPostcode;
    private final boolean postcodeNumeric;

    public CountryAndPostcodeValidation(PaymentForm paymentForm, boolean cardNumberValid, boolean cvvValid, boolean expiryDateValid, boolean maestroValid) {
        boolean postcodeValid = isPostcodeValid(paymentForm.getPostcode());

        this.postcodeEntryComplete = postcodeValid;
        this.showPostcodeError = !postcodeValid && !isEmpty(paymentForm.getPostcode());

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
        return !isEmpty(postcode);
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
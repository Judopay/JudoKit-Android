package com.judopay.validation;

import com.judopay.PaymentForm;
import com.judopay.R;
import com.judopay.model.Country;

import java.util.regex.Pattern;

@Deprecated
public class CountryAndPostcodeValidation {

    private static final Pattern ukPostcodePattern = Pattern.compile("\\b(GIR ?0AA|SAN ?TA1|(?:[A-PR-UWYZ](?:\\d{0,2}|[A-HK-Y]\\d|[A-HK-Y]\\d\\d|\\d[A-HJKSTUW]|[A-HK-Y]\\d[ABEHMNPRV-Y])) ?\\d[ABD-HJLNP-UW-Z]{2})\\b");
    private static final Pattern usZipCodePattern = Pattern.compile("(^\\\\d{5}$)|(^\\\\d{5}-\\\\d{4}$)");
    private static final Pattern canadaPostalCodePattern = Pattern.compile("[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]");

    private final int postcodeLabel;
    private final int postcodeError;
    private final boolean showPostcodeError;
    private final boolean postcodeEntryComplete;

    private final boolean showCountryAndPostcode;
    private final boolean postcodeEnabled;
    private final boolean postcodeNumeric;

    public CountryAndPostcodeValidation(PaymentForm paymentForm, boolean cardNumberValid, boolean securityCodeValid, boolean expiryDateValid, boolean maestroValid) {
        boolean postcodeValid = isPostcodeValid(paymentForm.getPostcode(), paymentForm.getCountry());

        this.postcodeEntryComplete = isPostcodeLengthValid(paymentForm.getPostcode().replaceAll("\\s+", ""), paymentForm.getCountry());
        this.showPostcodeError = !postcodeValid && postcodeEntryComplete;

        this.postcodeNumeric = Country.UNITED_STATES.equals(paymentForm.getCountry().getDisplayName());
        this.postcodeEnabled = !Country.OTHER.equals(paymentForm.getCountry().getDisplayName());
        this.showCountryAndPostcode = (paymentForm.isAddressRequired() && cardNumberValid && securityCodeValid && expiryDateValid) && (!paymentForm.isMaestroSupported() || maestroValid);
        this.postcodeLabel = getPostcodeLabel(paymentForm.getCountry());
        this.postcodeError = getPostcodeError(paymentForm.getCountry());
    }

    private boolean isPostcodeLengthValid(String postcode, Country country) {
        switch (country.getDisplayName()) {
            case Country.UNITED_KINGDOM:
            case Country.CANADA:
                return postcode.length() >= 6;
            case Country.UNITED_STATES:
                return postcode.length() >= 5;
        }
        return true;
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
        switch (country.getDisplayName()) {
            case Country.UNITED_KINGDOM:
                return ukPostcodePattern.matcher(postcode).matches();
            case Country.CANADA:
                return canadaPostalCodePattern.matcher(postcode).matches();
            case Country.UNITED_STATES:
                return usZipCodePattern.matcher(postcode).matches();
            default:
                return true;
        }
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
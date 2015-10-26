package com.judopay.payment.form;

import com.judopay.R;
import com.judopay.customer.Country;
import com.judopay.payment.form.address.CountryAndPostcodeValidation;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CountryAndPostcodeValidationTest {

    @Test
    public void shouldNotRequireCountryAndPostcodeWhenCardPartiallyEntered() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(true)
                        .build(), true, false, true);

        assertThat(countryAndPostcodeValidation.isCountryAndPostcodeRequired(), is(false));
    }

    @Test
    public void shouldRequireCountryAndPostcodeWhenCardDetailsEntered() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(true)
                        .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.isCountryAndPostcodeRequired(), is(true));
    }

    @Test
    public void shouldNotRequireCountryAndPostcodeWhenAddressNotRequired() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(false)
                        .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.isCountryAndPostcodeRequired(), is(false));
    }

    @Test
    public void shouldHavePostcodeLabelWhenUnitedKingdomCountrySelected() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(true)
                        .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.getPostcodeLabel(), is(R.string.postcode_uk));
    }

    @Test
    public void shouldHaveZipcodeLabelWhenUnitedStatesCountrySelected() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_STATES))
                        .setAddressRequired(true)
                        .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.getPostcodeLabel(), is(R.string.postcode_us));
    }

    @Test
    public void shouldHavePostalCodeLabelWhenCanadaCountrySelected() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                .setCardNumber("4282730000002397")
                .setCvv("789")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.CANADA))
                .setAddressRequired(true)
                .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.getPostcodeLabel(), is(R.string.postcode_canada));
    }

    @Test
    public void shouldHavePostcodeErrorWhenPostcodeNotEntered() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                .setCardNumber("4282730000002397")
                .setCvv("789")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .setAddressRequired(true)
                .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.getPostcodeError(), is(R.string.error_postcode_uk));
    }

    @Test
    public void shouldHaveZipCodeErrorWhenZipCodeNotEntered() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                .setCardNumber("4282730000002397")
                .setCvv("789")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_STATES))
                .setAddressRequired(true)
                .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.getPostcodeError(), is(R.string.error_postcode_us));
    }

    @Test
    public void shouldHavePostalCodeErrorWhenPostalCodeNotEntered() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                .setCardNumber("4282730000002397")
                .setCvv("789")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.CANADA))
                .setAddressRequired(true)
                .build(), true, true, true);

        assertThat(countryAndPostcodeValidation.getPostcodeError(), is(R.string.error_postcode_canada));
    }

}
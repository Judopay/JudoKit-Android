package com.judopay.payment.form;

import com.judopay.CountryAndPostcodeValidation;
import com.judopay.PaymentForm;
import com.judopay.R;
import com.judopay.model.Country;

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
                        .build(), true, false, true, true);

        assertThat(countryAndPostcodeValidation.isShowCountryAndPostcode(), is(false));
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
                        .build(), true, true, true, true);

        assertThat(countryAndPostcodeValidation.isShowCountryAndPostcode(), is(true));
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
                        .build(), true, true, true, true);

        assertThat(countryAndPostcodeValidation.isShowCountryAndPostcode(), is(false));
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
                        .build(), true, true, true, true);

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
                        .build(), true, true, true, true);

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
                .build(), true, true, true, true);

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
                .build(), true, true, true, true);

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
                .build(), true, true, true, true);

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
                .build(), true, true, true, true);

        assertThat(countryAndPostcodeValidation.getPostcodeError(), is(R.string.error_postcode_canada));
    }

    @Test
    public void shouldNotShowCountryAndPostcodeWhenMaestroInvalid() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.CANADA))
                        .setMaestroSupported(true)
                        .setAddressRequired(true)
                        .build(), true, true, true, false);

        assertThat(countryAndPostcodeValidation.isShowCountryAndPostcode(), is(false));
    }

    @Test
    public void shouldShowCountryAndPostcodeWhenMaestroValidAndAvsTurnedOn() {
        CountryAndPostcodeValidation countryAndPostcodeValidation = new CountryAndPostcodeValidation(
                new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("12/12")
                        .setIssueNumber("01")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.CANADA))
                        .setMaestroSupported(true)
                        .setAddressRequired(true)
                        .build(), true, true, true, true);

        assertThat(countryAndPostcodeValidation.isShowCountryAndPostcode(), is(true));
    }

}
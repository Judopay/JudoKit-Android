package com.judopay.payment.form;

import com.judopay.R;
import com.judopay.customer.Country;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PaymentFormViewTest {

    @Test
    public void shouldValidateCardNumberAsValid() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.isCardNumberValid(), is(true));
    }

    @Test
    public void shouldValidateCardNumberAsInvalid() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.isCardNumberValid(), is(false));
    }

    @Test
    public void shouldHaveValidExpiryDate() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("01/30")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.isExpiryDateValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidExpiryDate() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("09/15")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.isExpiryDateValid(), is(false));
    }

    @Test
    public void shouldHaveValidStartDate() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("01/30")
                        .setStartDate("09/15")
                        .setIssueNumber("01")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.isStartDateValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidStartDate() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("01/35")
                        .setStartDate("01/30")
                        .setIssueNumber("01")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.isStartDateValid(), is(false));
    }

    @Test
    public void shouldHaveCardNumberErrorWhenInvalid() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.getCardNumberError(), is(R.string.error_card_number));
    }

    @Test
    public void shouldHaveCidvHintFormatWhenAmex() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("340000146304174")
                        .setCvv("1234")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.getCvvHint(), is(R.string.amex_cvv_hint));
    }

    @Test
    public void shouldHaveCv2HintWhenNotAmex() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormView.getCvvHint(), is(R.string.cvv_hint));
    }

    @Test
    public void shouldHaveCardErrorWhenMaestroEnteredAndMaestroNotSupported() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("6759649826438453")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setMaestroSupported(false)
                        .build());

        assertThat(paymentFormView.isCardNumberValid(), is(false));
        assertThat(paymentFormView.getCardNumberError(), is(R.string.error_maestro_not_supported));
    }

    @Test
    public void shouldHaveCardErrorWhenAmexEnteredAndAmexNotSupported() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("34343434343434")
                        .setCvv("1234")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAmexSupported(false)
                        .build());

        assertThat(paymentFormView.isCardNumberValid(), is(false));
        assertThat(paymentFormView.getCardNumberError(), is(R.string.error_amex_not_supported));
    }

    @Test
    public void shouldRequireIssueNumberAndStartDateWhenMaestroEntered() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("6759649826438453")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setMaestroSupported(true)
                        .build());

        assertThat(paymentFormView.isIssueNumberAndStartDateRequired(), is(true));
    }

    @Test
    public void shouldNotRequireIssueNumberAndStartDateWhenMaestroEnteredButNotSupported() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("6759649826438453")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setMaestroSupported(false)
                        .build());

        assertThat(paymentFormView.isIssueNumberAndStartDateRequired(), is(false));
    }

    @Test
    public void shouldNotRequireCountryAndPostcodeWhenCardPartiallyEntered() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(true)
                        .build());

        assertThat(paymentFormView.isCountryAndPostcodeRequired(), is(false));
    }

    @Test
    public void shouldRequireCountryAndPostcodeWhenCardDetailsEntered() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(true)
                        .build());

        assertThat(paymentFormView.isCountryAndPostcodeRequired(), is(true));
    }

    @Test
    public void shouldNotRequireCountryAndPostcodeWhenAddressNotRequired() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(false)
                        .build());

        assertThat(paymentFormView.isCountryAndPostcodeRequired(), is(false));
    }

    @Test
    public void shouldHavePostcodeLabelWhenUnitedKingdomCountrySelected() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setAddressRequired(true)
                        .build());

        assertThat(paymentFormView.getPostcodeLabel(), is(R.string.postcode_uk));
    }

    @Test
    public void shouldHaveZipcodeLabelWhenUnitedStatesCountrySelected() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.UNITED_STATES))
                        .setAddressRequired(true)
                        .build());

        assertThat(paymentFormView.getPostcodeLabel(), is(R.string.postcode_us));
    }

    @Test
    public void shouldHavePostalCodeLabelWhenCanadaCountrySelected() {
        PaymentFormView paymentFormView = new PaymentFormView.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("789")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setCountry(new Country(0, Country.CANADA))
                        .setAddressRequired(true)
                        .build());

        assertThat(paymentFormView.getPostcodeLabel(), is(R.string.postcode_canada));
    }

}
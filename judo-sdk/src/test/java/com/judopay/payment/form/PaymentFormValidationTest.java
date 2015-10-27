package com.judopay.payment.form;

import com.judopay.R;
import com.judopay.customer.Country;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PaymentFormValidationTest {

    @Test
    public void shouldHaveValidExpiryDate() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("01/30")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormValidation.isShowExpiryDateError(), is(false));
    }

    @Test
    public void shouldHaveInvalidExpiryDate() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("1234567812345678")
                        .setCvv("123")
                        .setExpiryDate("09/15")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormValidation.isShowExpiryDateError(), is(true));
    }

    @Test
    public void shouldHaveCidvHintFormatWhenAmex() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("340000146304174")
                        .setCvv("1234")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormValidation.getCvvHint(), is(R.string.amex_cvv_hint));
    }

    @Test
    public void shouldHaveCv2HintWhenNotAmex() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("123")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormValidation.getCvvHint(), is(R.string.cvv_hint));
    }

    @Test
    public void shouldHaveFourCharacterCvvLengthWhenAmex() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("340000146304174")
                        .setCvv("1234")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormValidation.getCvvLength(), is(4));
    }

    @Test
    public void shouldHaveThreeCharacterCvvLengthWhenNotAmex() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("1234")
                        .setExpiryDate("12/99")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormValidation.getCvvLength(), is(3));
    }

    @Test
    public void shouldHavePaymentButtonDisabledWhenFormStarted() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("")
                        .setExpiryDate("")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.CANADA))
                        .build());

        assertThat(paymentFormValidation.isPaymentButtonEnabled(), is(false));
    }

    @Test
    public void shouldHavePaymentButtonEnabledWhenCardDetailsEntered() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("123")
                        .setExpiryDate("12/30")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.CANADA))
                        .build());

        assertThat(paymentFormValidation.isPaymentButtonEnabled(), is(true));
    }

    @Test
    public void shouldNotHavePaymentButtonEnabledWhenMaestroCardDetailsIncomplete() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("6759649826438453")
                        .setCvv("123")
                        .setExpiryDate("12/30")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setMaestroSupported(true)
                        .setCountry(new Country(0, Country.CANADA))
                        .build());

        assertThat(paymentFormValidation.isPaymentButtonEnabled(), is(false));
    }

    @Test
    public void shouldHavePaymentButtonEnabledWhenMaestroCardDetailsComplete() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("6759649826438453")
                        .setCvv("123")
                        .setExpiryDate("12/30")
                        .setStartDate("12/12")
                        .setIssueNumber("01")
                        .setPostcode("")
                        .setMaestroSupported(true)
                        .setCountry(new Country(0, Country.CANADA))
                        .build());

        assertThat(paymentFormValidation.isPaymentButtonEnabled(), is(true));
    }

    @Test
    public void shouldHavePaymentButtonDisabledWhenAddressIncomplete() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("123")
                        .setExpiryDate("12/30")
                        .setStartDate("12/12")
                        .setIssueNumber("01")
                        .setPostcode("")
                        .setAddressRequired(true)
                        .setCountry(new Country(0, Country.OTHER))
                        .build());

        assertThat(paymentFormValidation.isPaymentButtonEnabled(), is(false));
    }

    @Test
    public void shouldHavePaymentButtonEnabledWhenAddressComplete() {
        PaymentFormValidation paymentFormValidation = new PaymentFormValidation.Builder()
                .build(new PaymentForm.Builder()
                        .setCardNumber("4282730000002397")
                        .setCvv("123")
                        .setExpiryDate("12/30")
                        .setStartDate("12/12")
                        .setIssueNumber("01")
                        .setPostcode("SW1A 1AA")
                        .setAddressRequired(true)
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .build());

        assertThat(paymentFormValidation.isPaymentButtonEnabled(), is(true));
    }

}
package com.judopay.payment.form;

import com.judopay.R;
import com.judopay.customer.Country;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StartDateAndIssueNumberValidationTest {

    @Test
    public void shouldHaveValidStartDate() {
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(new PaymentForm.Builder()
                .setCardNumber("1234567812345678")
                .setCvv("123")
                .setExpiryDate("01/30")
                .setStartDate("09/15")
                .setIssueNumber("01")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .build(), true);

        assertThat(startDateAndIssueNumberValidation.isStartDateEntryComplete(), is(true));
    }

    @Test
    public void shouldHaveInvalidStartDate() {
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(new PaymentForm.Builder()
                .setCardNumber("1234567812345678")
                .setCvv("123")
                .setExpiryDate("01/35")
                .setStartDate("01/30")
                .setIssueNumber("01")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .build(), true);

        assertThat(startDateAndIssueNumberValidation.isShowStartDateError(), is(true));
        assertThat(startDateAndIssueNumberValidation.getStartDateError(), is(R.string.error_check_date));
    }

    @Test
    public void shouldRequireIssueNumberAndStartDateWhenMaestroEntered() {
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(new PaymentForm.Builder()
                .setCardNumber("6759649826438453")
                .setCvv("123")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .setMaestroSupported(true)
                .build(), true);

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(true));
    }

    @Test
    public void shouldNotRequireIssueNumberAndStartDateWhenMaestroEnteredButNotSupported() {
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(new PaymentForm.Builder()
                .setCardNumber("6759649826438453")
                .setCvv("123")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .setMaestroSupported(false)
                .build(), true);

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(false));
    }

    @Test
    public void shouldNotShowIssueNumberAndStartDateBeforeCardNumberEntered() {
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(
                new PaymentForm.Builder()
                        .setCardNumber("")
                        .setCvv("")
                        .setExpiryDate("")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setMaestroSupported(true)
                        .build(), false);

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(false));
    }

    @Test
    public void shouldShowIssueNumberAndStartDateWhenCardNumberEntered() {
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(
                new PaymentForm.Builder()
                        .setCardNumber("6759649826438453")
                        .setCvv("")
                        .setExpiryDate("")
                        .setStartDate("")
                        .setIssueNumber("")
                        .setPostcode("")
                        .setCountry(new Country(0, Country.UNITED_KINGDOM))
                        .setMaestroSupported(true)
                        .build(), true);

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(true));
    }

}
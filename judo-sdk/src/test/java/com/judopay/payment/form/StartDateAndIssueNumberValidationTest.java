package com.judopay.payment.form;

import com.judopay.PaymentForm;
import com.judopay.R;
import com.judopay.StartDateAndIssueNumberValidation;
import com.judopay.model.CardType;
import com.judopay.model.Country;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StartDateAndIssueNumberValidationTest {

    @Test
    public void shouldHaveValidStartDate() {
        final PaymentForm build = new PaymentForm.Builder()
                .setCardNumber("1234567812345678")
                .setCvv("123")
                .setExpiryDate("01/30")
                .setStartDate("09/15")
                .setIssueNumber("01")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .build();
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(build, CardType.fromCardNumber(build.getCardNumber()));

        assertThat(startDateAndIssueNumberValidation.isStartDateEntryComplete(), is(true));
    }

    @Test
    public void shouldHaveInvalidStartDate() {
        final PaymentForm build = new PaymentForm.Builder()
                .setCardNumber("1234567812345678")
                .setCvv("123")
                .setExpiryDate("01/35")
                .setStartDate("01/30")
                .setIssueNumber("01")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .build();
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(build, CardType.fromCardNumber(build.getCardNumber()));

        assertThat(startDateAndIssueNumberValidation.isShowStartDateError(), is(true));
        assertThat(startDateAndIssueNumberValidation.getStartDateError(), is(R.string.check_start_date));
    }

    @Test
    public void shouldRequireIssueNumberAndStartDateWhenMaestroEntered() {
        final PaymentForm build = new PaymentForm.Builder()
                .setCardNumber("6759649826438453")
                .setCvv("123")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .setMaestroSupported(true)
                .build();
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(build, CardType.fromCardNumber(build.getCardNumber()));

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(true));
    }

    @Test
    public void shouldNotRequireIssueNumberAndStartDateWhenMaestroEnteredButNotSupported() {
        final PaymentForm build = new PaymentForm.Builder()
                .setCardNumber("6759649826438453")
                .setCvv("123")
                .setExpiryDate("12/99")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .setMaestroSupported(false)
                .build();
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(build, CardType.fromCardNumber(build.getCardNumber()));

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(false));
    }

    @Test
    public void shouldNotShowIssueNumberAndStartDateBeforeCardNumberEntered() {
        final PaymentForm build = new PaymentForm.Builder()
                .setCardNumber("")
                .setCvv("")
                .setExpiryDate("")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .setMaestroSupported(true)
                .build();
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(
                build, CardType.fromCardNumber(build.getCardNumber()));

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(false));
    }

    @Test
    public void shouldShowIssueNumberAndStartDateWhenCardNumberPartiallyEntered() {
        final PaymentForm build = new PaymentForm.Builder()
                .setCardNumber("6759")
                .setCvv("")
                .setExpiryDate("")
                .setStartDate("")
                .setIssueNumber("")
                .setPostcode("")
                .setCountry(new Country(0, Country.UNITED_KINGDOM))
                .setMaestroSupported(true)
                .build();
        StartDateAndIssueNumberValidation startDateAndIssueNumberValidation = new StartDateAndIssueNumberValidation(
                build, CardType.fromCardNumber(build.getCardNumber()));

        assertThat(startDateAndIssueNumberValidation.isShowIssueNumberAndStartDate(), is(true));
    }

}
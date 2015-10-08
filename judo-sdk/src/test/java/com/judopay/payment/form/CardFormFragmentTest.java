package com.judopay.payment.form;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.judopay.BuildConfig;
import com.judopay.Consumer;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.payment.Payment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CardFormFragmentTest {

    public static final String VISA_CARD_NUMBER = "4976000000003436";
    public static final String MAESTRO_CARD_NUMBER = "6759000000005462";
    public static final String AMEX_CARD_NUMBER = "340000432128428";

    public static final String EXPIRY_DATE = "12/15";
    public static final String CV2 = "452";
    public static final String START_DATE = "01/12";
    public static final String ISSUE_NUMBER = "01";
    public static final String POSTCODE = "SW16";

    @Test
    public void shouldShowCountrySpinnerWhenAvsEnabled() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(true);

        CardFormFragment fragment = getFragment();

        startFragment(fragment);

        View countrySpinner = findView(fragment.getView(), R.id.country_spinner);

        assertThat(countrySpinner.getVisibility(), equalTo(View.VISIBLE));
    }

    @Test
    public void shouldHideCountrySpinnerWhenAvsDisabled() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();

        startFragment(fragment);

        View countrySpinner = findView(fragment.getView(), R.id.country_spinner);

        assertThat(countrySpinner.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldShowIssueNumberAndStartDateFieldsWhenMaestro() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();
        startFragment(fragment);

        View fragmentView = fragment.getView();

        enterCardNumber(fragmentView, MAESTRO_CARD_NUMBER);

        View issueNumberView = findView(fragmentView, R.id.issue_number_edit_text);
        assertThat(issueNumberView.getVisibility(), equalTo(View.VISIBLE));

        View startDateView = findView(fragmentView, R.id.start_date_edit_text);
        assertThat(startDateView.getVisibility(), equalTo(View.VISIBLE));
    }

    @Test
    public void shouldHideIssueNumberAndStartDateFieldsWhenNoLongerMaestro() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();
        startFragment(fragment);

        View fragmentView = fragment.getView();

        EditText cardNumberEditText = findView(fragmentView, R.id.card_number_edit_text);

        cardNumberEditText.setText(MAESTRO_CARD_NUMBER);
        cardNumberEditText.setText(VISA_CARD_NUMBER);

        View issueNumberView = findView(fragmentView, R.id.issue_number_edit_text);
        assertThat(issueNumberView.getVisibility(), equalTo(View.GONE));

        View startDateView = findView(fragmentView, R.id.start_date_edit_text);
        assertThat(startDateView.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldEnablePayButtonWhenFormValid() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();
        startFragment(fragment);

        View fragmentView = fragment.getView();

        View paymentButton = findView(fragmentView, R.id.payment_button);
        assertThat(paymentButton.isEnabled(), is(false));

        enterCardDetails(fragmentView, VISA_CARD_NUMBER, EXPIRY_DATE, CV2);

        assertThat(paymentButton.isEnabled(), is(true));
    }

    @Test
    public void shouldEnablePayButtonWhenFormValidAndAvsEnabled() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(true);

        CardFormFragment fragment = getFragment();
        startFragment(fragment);

        View fragmentView = fragment.getView();

        View paymentButton = findView(fragmentView, R.id.payment_button);
        assertThat(paymentButton.isEnabled(), is(false));

        enterCardDetails(fragmentView, VISA_CARD_NUMBER, EXPIRY_DATE, CV2);

        EditText postcodeEditText = findView(fragmentView, R.id.post_code_edit_text);
        postcodeEditText.setText(POSTCODE);

        Spinner countrySpinner = findView(fragmentView, R.id.country_spinner);
        countrySpinner.setSelection(0);

        assertThat(paymentButton.isEnabled(), is(true));
    }

    @Test
    public void shouldEnablePayButtonWhenFormValidAndMaestroCardEntered() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();
        startFragment(fragment);

        View fragmentView = fragment.getView();

        View paymentButton = findView(fragmentView, R.id.payment_button);
        assertThat(paymentButton.isEnabled(), is(false));

        enterCardDetails(fragmentView, MAESTRO_CARD_NUMBER, EXPIRY_DATE, CV2);

        EditText startDateEditText = findView(fragmentView, R.id.start_date_edit_text);
        startDateEditText.setText(START_DATE);

        EditText issueNumberEditText = findView(fragmentView, R.id.issue_number_edit_text);
        issueNumberEditText.setText(ISSUE_NUMBER);

        assertThat(paymentButton.isEnabled(), is(true));
    }

    @Test
    public void shouldDisablePayButtonWhenInvalidCidvEntered() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();
        startFragment(fragment);

        View fragmentView = fragment.getView();

        enterCardDetails(fragmentView, AMEX_CARD_NUMBER, EXPIRY_DATE, "10001");

        View paymentButton = findView(fragmentView, R.id.payment_button);
        assertThat(paymentButton.isEnabled(), is(false));
    }

    @Test
    public void shouldDisablePayButtonWhenInvalidCv2Entered() {
        JudoPay.setup(RuntimeEnvironment.application, null, null, 0);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();
        startFragment(fragment);

        View fragmentView = fragment.getView();

        enterCardDetails(fragmentView, VISA_CARD_NUMBER, EXPIRY_DATE, "1000");

        View paymentButton = findView(fragmentView, R.id.payment_button);
        assertThat(paymentButton.isEnabled(), is(false));
    }

    @SuppressWarnings("unchecked")
    public <T> T findView(View view, int viewId) {
        return (T) view.findViewById(viewId);
    }

    @NonNull
    private CardFormFragment getFragment() {
        CardFormFragment fragment = new CardFormFragment();
        Payment payment = new Payment.Builder()
                .setAmount(100)
                .setConsumer(new Consumer("consumerRef"))
                .setPaymentRef("paymentRef")
                .setCurrency("GBP")
                .setJudoId(123456)
                .build();

        Bundle bundle = new Bundle();
        bundle.putParcelable(JudoPay.EXTRA_PAYMENT, payment);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void enterCardDetails(View fragmentView, String cardNumber, String expiryDate, String cvv) {
        enterCardNumber(fragmentView, cardNumber);

        EditText expiryDateEditText = findView(fragmentView, R.id.expiry_date_edit_text);
        expiryDateEditText.setText(expiryDate);

        EditText cvvEditText = findView(fragmentView, R.id.cvv_edit_text);
        cvvEditText.setText(cvv);
    }

    private void enterCardNumber(View fragmentView, String amexCardNumber) {
        EditText cardNumberEditText = findView(fragmentView, R.id.card_number_edit_text);
        cardNumberEditText.setText(amexCardNumber);
    }

}
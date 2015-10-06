package com.judopay.payment.form;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

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

import static com.judopay.JudoPay.Environment.SANDBOX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class CardFormFragmentTest {

    @Test
    public void shouldShowCountrySpinnerWhenAvsEnabled() {
        JudoPay.setup(RuntimeEnvironment.application, "apiToken", "apiSecret", SANDBOX);
        JudoPay.setAvsEnabled(true);

        CardFormFragment fragment = getFragment();

        startFragment(fragment);

        View countrySpinner = fragment.getView().findViewById(R.id.country_spinner);

        assertThat(countrySpinner.getVisibility(), equalTo(View.VISIBLE));
    }

    @Test
    public void shouldHideCountrySpinnerWhenAvsDisabled() {
        JudoPay.setup(RuntimeEnvironment.application, "apiToken", "apiSecret", SANDBOX);
        JudoPay.setAvsEnabled(false);

        CardFormFragment fragment = getFragment();

        startFragment(fragment);

        View countrySpinner = fragment.getView().findViewById(R.id.country_spinner);

        assertThat(countrySpinner.getVisibility(), equalTo(View.GONE));
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

}
package com.judopay.ui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.model.Currency;

import org.junit.Rule;
import org.junit.Test;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.ui.util.ViewMatchers.withTextInputHint;

@LargeTest
public class SecurityCodeHintTest {

    @Rule
    public ActivityTestRule<PaymentActivity> rule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisplayCvvSecurityCodeWhenUnknownCard() {
        rule.launchActivity(getIntent());

        onView(withId(R.id.security_code_input_layout))
                .check(matches(withTextInputHint("CVV")));
    }

    @Test
    public void shouldDisplayVisaSecurityCodeWhenVisaDetected() {
        rule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.security_code_input_layout))
                .check(matches(withTextInputHint("CVV2")));
    }


    @Test
    public void shouldDisplayAmexSecurityCodeWhenAmexDetected() {
        Judo.setAmexEnabled(true);

        rule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.security_code_input_layout))
                .check(matches(withTextInputHint("CID")));
    }

    @Test
    public void shouldDisplayMastercardSecurityCodeWhenMastercardDetected() {
        Judo.setAmexEnabled(true);

        rule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("5100000000005460"));

        onView(withId(R.id.security_code_input_layout))
                .check(matches(withTextInputHint("CVC2")));
    }

    private Intent getIntent() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef(UUID.randomUUID().toString())
                .build());
        return intent;
    }

}
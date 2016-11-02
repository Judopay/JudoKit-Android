package com.judopay;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.model.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class PrefillCardDataTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldHaveVisaCardNumberEnteredInPaymentForm() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
                .setEnvironment(Judo.UAT)
                .setJudoId("00000000")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setCardNumber("4934123412341234")
                .setExpiryMonth("01")
                .setExpiryYear("20")
                .setReference(UUID.randomUUID().toString())
                .build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .check(matches(withText("4934 1234 1234 1234")));
    }

    @Test
    public void shouldHaveAmexCardNumberEnteredInPaymentForm() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
                .setEnvironment(Judo.UAT)
                .setJudoId("00000000")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setCardNumber("343412341234123")
                .setExpiryMonth("01")
                .setExpiryYear("20")
                .setReference(UUID.randomUUID().toString())
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .check(matches(withText("3434 123412 34123")));
    }


}

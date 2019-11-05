package com.judopay;

import android.content.Intent;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.TestUtil.getJudo;

@RunWith(AndroidJUnit4.class)
public class PrefillCardDataTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldHaveVisaCardNumberEnteredInPaymentForm() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setCardNumber("4934123412341234")
                .setExpiryMonth("01")
                .setExpiryYear("20")
                .setConsumerReference(UUID.randomUUID().toString())
                .build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .check(matches(withText("4934 1234 1234 1234")));
    }

    @Test
    public void shouldHaveAmexCardNumberEnteredInPaymentForm() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setCardNumber("343412341234123")
                .setExpiryMonth("01")
                .setExpiryYear("20")
                .setConsumerReference(UUID.randomUUID().toString())
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .check(matches(withText("3434 123412 34123")));
    }


}

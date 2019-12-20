package com.judopay;

import android.content.Intent;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;
import static com.judopay.util.ViewMatchers.withTextInputHint;

public class SecurityCodeHintTest {

    @Rule
    public ActivityTestRule<PaymentActivity> rule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisplayCvvSecurityCodeWhenUnknownCard() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        rule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .check(matches(withHint("CVV")));
    }

    @Test
    public void shouldDisplayVisaSecurityCodeWhenVisaDetected() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        rule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976"));

        onView(withId(R.id.securityCodeEditText))
                .check(matches(withHint("CVV2")));
    }

    @Test
    public void shouldDisplayAmexSecurityCodeWhenAmexDetected() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        rule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("3400"));

        onView(withId(R.id.securityCodeEditText))
                .check(matches(withHint("CID")));
    }

    @Test
    public void shouldDisplayMastercardSecurityCodeWhenMastercardDetected() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        rule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("5100"));

        onView(withId(R.id.securityCodeEditText))
                .check(matches(withHint("CVC2")));
    }
}
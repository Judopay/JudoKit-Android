package com.judopay;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.model.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AutoMoveToNextFieldWhenValidDetailsEnteredTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldMoveToExpiryDateFieldWhenValidCardNumberEntered() {
        Judo.setAvsEnabled(false);
        Judo.setMaestroEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldMoveToCvvFieldWhenValidExpiryDateEntered() {
        Judo.setAvsEnabled(false);
        Judo.setMaestroEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldMoveToPostcodeFieldWhenValidCardDetailsEntered() {
        Judo.setAvsEnabled(true);
        Judo.setMaestroEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldMoveToStartDateFieldWhenValidMaestroCardNumberEntered() {
        Judo.setMaestroEnabled(true);
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldMoveToIssueNumberFieldWhenMaestroAndValidStartDateEntered() {
        Judo.setMaestroEnabled(true);
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldMoveToCvvFieldWhenMaestroAndValidExpiryDateEntered() {
        Judo.setMaestroEnabled(true);
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldMoveToPostcodeFieldWhenMaestroAndValidCardDetailsEntered() {
        Judo.setMaestroEnabled(true);
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("789"));

        onView(withId(R.id.post_code_edit_text))
                .check(matches(hasFocus()));
    }

    private Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef(UUID.randomUUID().toString())
                .build());

        return intent;
    }
}
package com.judopay.ui;

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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class JudoOptionsTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldShowCustomButtonLabel() {
        Intent intent = new Intent();

        String buttonLabel = "Button Label";
        intent.putExtra(Judo.JUDO_OPTIONS, getJudoOptionsBuilder()
                .setButtonLabel(buttonLabel)
                .build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.payment_button))
                .check(matches(withText(buttonLabel)));
    }

    @Test
    public void shouldDisplaySecureServerMessage() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudoOptionsBuilder()
                .setSecureServerMessageShown(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.secure_server_text))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.secure_server_transmission)));
    }

    @Test
    public void shouldShowActivityTitle() {
        Intent intent = new Intent();
        String activityTitle = "Activity title";

        intent.putExtra(Judo.JUDO_OPTIONS, getJudoOptionsBuilder()
                .setActivityTitle(activityTitle)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withText(activityTitle))
                .check(matches(isDisplayed()));
    }

    public JudoOptions.Builder getJudoOptionsBuilder() {
        return new JudoOptions.Builder()
                .setJudoId("000000")
                .setAmount("0.01")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef");
    }

}

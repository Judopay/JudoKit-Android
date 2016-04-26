package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.model.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PaymentFormTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisplayCidvHintWhenAmexCardNumberEntered() {
        Judo.setAmexEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.security_code_edit_text))
                .perform(click())
                .check(matches(withHint("0000")));
    }

    @Test
    public void shouldDisplayCv2HintWhenVisaCardNumberEntered() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.security_code_edit_text))
                .perform(click())
                .check(matches(withHint("000")));
    }

    @Test
    public void shouldDisplayCvvImageOnLaunch() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.security_code_image_view))
                .check(matches(isDisplayed()));
    }

    protected Intent getIntent() {
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
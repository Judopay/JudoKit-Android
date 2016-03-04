package com.judopay.ui;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.R;
import com.judopay.RegisterCardActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegisterCardTest {

    @Rule
    public IntentsTestRule<RegisterCardActivity> activityTestRule = new IntentsTestRule<>(RegisterCardActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setEnvironment(Judo.Environment.SANDBOX);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidVisaEntered() {
        Judo.setAvsEnabled(false);
        activityTestRule.launchActivity(getIntent());

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("125"));

        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withText(R.string.add_card_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setConsumerRef("consumerRef")
                .build());

        return intent;
    }
}

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
public class DeclinedPaymentTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);
    
    @Test
    public void shouldDeclineInvalidVisaCard() {
        Judo.setAvsEnabled(false);
        Judo.setMaestroEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("125"));

        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidMaestroCard() {
        Judo.setAvsEnabled(false);
        Judo.setMaestroEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000009076"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("671"));

        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidAmexCard() {
        Judo.setAmexEnabled(true);
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000150358074"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("7654"));


        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidVisaCardWhenAvsEnabled() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("125"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("N226JN"));

        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidMaestroWhenAvsEnabled() {
        Judo.setAvsEnabled(true);
        Judo.setMaestroEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000009076"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("671"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("OX143AL"));

        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidAmexWhenAvsEnabled() {
        Judo.setAmexEnabled(true);
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000150358074"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("7654"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("TQ11BH"));

        onView(withId(R.id.payment_button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    protected Intent getIntent() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .build());
        return intent;
    }

}

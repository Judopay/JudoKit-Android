package com.judopay.payment;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.R;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DeclinedPaymentTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDeclineInvalidVisaCard() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("125"));

        onView(withId(R.id.button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidMaestroCard() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000009076"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("671"));

        onView(withId(R.id.button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidAmexCard() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000150358074"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("7654"));

        onView(withId(R.id.button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidVisaCardWhenAvsEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("125"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("N226JN"));

        onView(withId(R.id.button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidMaestroWhenAvsEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000009076"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("671"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("OX143AL"));

        onView(withId(R.id.button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDeclineInvalidAmexWhenAvsEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000150358074"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("7654"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("TQ11BH"));

        onView(withId(R.id.button))
                .perform(click());

        onView(withText(R.string.payment_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));
    }


    private Judo.Builder getJudo() {
        return new Judo.Builder()
                .setEnvironment(Judo.UAT)
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef(UUID.randomUUID().toString());
    }

}
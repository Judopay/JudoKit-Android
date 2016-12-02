package com.judopay.error;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class PaymentFormErrorMessageTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisplayErrorMessageWhenInvalidCardNumberEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("1234000000001234"));

        onView(withText(R.string.check_card_number))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenPastExpiryDateEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("0101"));

        onView(withText(R.string.check_expiry_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorWhenExpiryDateMoreThanTenYearsInFuture() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1230"));

        onView(withText(R.string.check_expiry_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenFutureStartDateEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0125"));

        onView(withText(R.string.check_start_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenStartDateOlderThanTenYearsEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("1205"));

        onView(withText(R.string.check_start_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayAmexNotSupportedWhenAmexEnteredAndNotEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .setAmexEnabled(false)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("3400"));

        onView(withText(R.string.error_amex_not_supported))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayMaestroNotSupportedWhenMaestroEnteredAndNotEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .setMaestroEnabled(false)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759"));

        onView(withText(R.string.error_maestro_not_supported))
                .check(matches(isDisplayed()));
    }

    private Judo.Builder getJudo() {
        return new Judo.Builder()
                .setEnvironment(Judo.SANDBOX)
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setAvsEnabled(true)
                .setConsumerRef(UUID.randomUUID().toString());
    }

}

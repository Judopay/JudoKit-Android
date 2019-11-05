package com.judopay.error;

import android.content.Intent;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.TestUtil.getJudo;

@RunWith(AndroidJUnit4.class)
public class PaymentFormErrorMessageTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisplayErrorMessageWhenInvalidCardNumberEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("1234000000001234"));

        onView(withText(R.string.check_card_number))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenPastExpiryDateEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("0101"));

        onView(withText(R.string.check_expiry_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorWhenExpiryDateMoreThanTenYearsInFuture() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1230"));

        onView(withText(R.string.check_expiry_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenFutureStartDateEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

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
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

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
                .newBuilder()
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
                .newBuilder()
                .setMaestroEnabled(false)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759"));

        onView(withText(R.string.error_maestro_not_supported))
                .check(matches(isDisplayed()));
    }

}
package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.model.Country;
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
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.util.JudoViewMatchers.isDisabled;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PaymentFormErrorMessageTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setup("fakeApiToken", "fakeApiSecret", Judo.Environment.SANDBOX);
    }

    @Test
    public void shouldDisplayErrorMessageWhenInvalidCardNumberEntered() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("1234000000001234"));

        onView(withText(R.string.error_card_number))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenPastExpiryDateEntered() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("0101"));

        onView(withText(R.string.error_check_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorWhenExpiryDateMoreThanTenYearsInFuture() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1230"));

        onView(withText(R.string.error_check_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenFutureStartDateEntered() {
        Judo.setMaestroEnabled(true);
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0125"));

        onView(withText(R.string.error_check_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenStartDateOlderThanTenYearsEntered() {
        Judo.setMaestroEnabled(true);
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("1205"));

        onView(withText(R.string.error_check_date))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayAmexNotSupportedWhenAmexEnteredAndNotEnabled() {
        Judo.setAmexEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("3400"));

        onView(withText(R.string.error_amex_not_supported))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayMaestroNotSupportedWhenMaestroEnteredAndNotEnabled() {
        Judo.setMaestroEnabled(false);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759"));

        onView(withText(R.string.error_maestro_not_supported))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayErrorMessageWhenInvalidBillingCountryChosen() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .perform(click());

        onView(withText(Country.OTHER))
                .perform(click());

        onView(withId(R.id.post_code_edit_text))
                .check(matches(isDisabled()));
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

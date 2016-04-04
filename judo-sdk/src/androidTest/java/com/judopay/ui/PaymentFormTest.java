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
import com.judopay.model.Country;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.ui.util.ViewMatchers.isNotDisplayed;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PaymentFormTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisplayCidvHintWhenAmexCardNumberEntered() {
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
    public void shouldDisablePostcodeFieldWhenOtherCountrySelected() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));
    }

    @Test
    public void shouldShowPaymentButtonWhenAvsEnabledAndOtherCountrySelected() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .perform(click());

        onView(withText(Country.OTHER))
                .perform(click());

        onView(withId(R.id.payment_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldNotShowPaymentButtonWhenAvsEnabledAndCountrySelected() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .perform(click());

        onView(withText(Country.UNITED_KINGDOM))
                .perform(click());

        onView(withId(R.id.payment_button))
                .check(matches(isNotDisplayed()));
    }

    @Test
    public void shouldNotAllowPaymentWhenAvsEnabledAndPostcodeNotEntered() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .perform(click());

        onView(withText(Country.UNITED_KINGDOM))
                .perform(click());

        onView(withId(R.id.payment_button))
                .check(matches(isNotDisplayed()));
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
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef(UUID.randomUUID().toString())
                .build());
        return intent;
    }

}
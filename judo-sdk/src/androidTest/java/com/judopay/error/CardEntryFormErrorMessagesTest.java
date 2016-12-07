package com.judopay.error;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.model.Country;

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
import static com.judopay.TestUtil.getJudo;

@RunWith(AndroidJUnit4.class)
public class CardEntryFormErrorMessagesTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldShowErrorWhenInvalidUkPostcodeEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

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

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("123456"));

        onView(withText(R.string.error_postcode_uk))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowErrorWhenInvalidCanadianPostcodeEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);
        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .perform(click());

        onView(withText(Country.CANADA))
                .perform(click());

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("123456"));

        onView(withText(R.string.error_postcode_canada))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowErrorWhenInvalidUsZipCodeEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .perform(click());

        onView(withText(Country.UNITED_STATES))
                .perform(click());

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("123456"));

        onView(withText(R.string.error_postcode_us))
                .check(matches(isDisplayed()));
    }

}
package com.judopay.avs;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.judopay.R;
import com.judopay.model.Country;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;
import static com.judopay.util.ViewMatchers.isDisabled;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class AddressVerificationTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisablePostcodeFieldWhenOtherCountrySelected() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));
    }

    @Test
    public void shouldShowPaymentButtonWhenAvsEnabledAndOtherCountrySelected() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withId(R.id.countrySpinner))
                .perform(click());

        onData(allOf(is(instanceOf(Country.class)), is(Country.OTHER)))
                .inRoot(isPlatformPopup())
                .perform(click());


        onView(withId(R.id.entryButton))
                .check(matches(isEnabled()));
    }

    @Test
    public void shouldNotShowPaymentButtonWhenAvsEnabledAndCountrySelected() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withId(R.id.countrySpinner))
                .perform(click());

        onData(allOf(is(instanceOf(Country.class)), is(Country.UNITED_KINGDOM)))
                .inRoot(isPlatformPopup())
                .perform(click());


        onView(withId(R.id.entryButton))
                .check(matches(isDisabled()));
    }

    @Test
    public void shouldNotAllowPaymentWhenAvsEnabledAndPostcodeNotEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withId(R.id.countrySpinner))
                .perform(click());

        onData(allOf(is(instanceOf(Country.class)), is(Country.UNITED_KINGDOM)))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.entryButton))
                .check(matches(isDisabled()));
    }

}
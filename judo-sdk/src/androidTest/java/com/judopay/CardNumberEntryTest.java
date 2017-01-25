package com.judopay;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.TestUtil.getJudo;

public class CardNumberEntryTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    @Ignore
    public void shouldHaveAmexCardNumberFormattingWhenAmexCardEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"))
                .check(matches(withText("3400 004321 28428")));
    }

    @Test
    @Ignore
    public void shouldHaveNormalCardNumberFormattingWhenVisaCardNumberEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"))
                .check(matches(withText("4976 0000 0000 3436")));
    }

    @Test
    @Ignore
    public void shouldRestrictCardNumberLengthToSixteenDigitsWhenVisa() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("49760000000034360"))
                .check(matches(withText("4976 0000 0000 3436")));
    }

    @Test
    @Ignore
    public void shouldRestrictCardNumberLengthToFifteenDigitsWhenAmex() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("3400004321284280"))
                .check(matches(withText("3400 004321 28428")));
    }

    @Test
    @Ignore
    public void shouldNotAllowSpacesAtStartOfCardNumber() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText(" 1234"))
                .check(matches(withText("1234 ")));
    }
}
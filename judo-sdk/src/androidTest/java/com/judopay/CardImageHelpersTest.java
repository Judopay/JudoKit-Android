package com.judopay;

import android.content.Intent;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;

@RunWith(AndroidJUnit4.class)
public class CardImageHelpersTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldShowCardTypeImageAsOpaqueAfterCardNumberEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976"));

        onView(withId(R.id.expiryDateEditText))
                .perform(click());

        onView(withId(R.id.cardNumberImageView))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void shouldNotShowCardImageAfterDeletedAndNotFocused() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976"))
                .perform(replaceText(""));

        onView(withId(R.id.expiryDateEditText))
                .perform(click());

        onView(withId(R.id.cardNumberImageView))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

}

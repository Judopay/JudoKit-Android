package com.judopay.theme;

import android.content.Intent;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.TestUtil.getJudo;
import static com.judopay.util.ViewMatchers.withResourceName;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ButtonLabelTest {

    @Rule
    public ActivityTestRule<RegisterCardActivity> testRule = new ActivityTestRule<>(RegisterCardActivity.class, false, false);

    @Rule
    public ActivityTestRule<PreAuthActivity> preAuthTestRule = new ActivityTestRule<>(PreAuthActivity.class, false, false);

    @Test
    public void shouldDisplayAddCardButtonLabel() {
        testRule.launchActivity(getIntent());

        onView(ViewMatchers.withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        // multiple views with add card text, so need to be specific
        onView(allOf(isDescendantOfA(withResourceName("android:id/content")), withText(R.string.add_card)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayPayButtonLabel() {
        preAuthTestRule.launchActivity(getIntent());

        onView(ViewMatchers.withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withText(R.string.pay))
                .check(matches(isDisplayed()));
    }

    private Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());
        return intent;
    }

}
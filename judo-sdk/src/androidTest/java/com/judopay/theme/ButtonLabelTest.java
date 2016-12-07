package com.judopay.theme;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PreAuthActivity;
import com.judopay.R;
import com.judopay.RegisterCardActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        // multiple views with add card text, so need to be specific
        onView(allOf(isDescendantOfA(withResourceName("android:id/content")), withText(R.string.add_card)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayPayButtonLabel() {
        preAuthTestRule.launchActivity(getIntent());

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
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
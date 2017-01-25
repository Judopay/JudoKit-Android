package com.judopay.registercard;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.JudoTransactionIdlingResource;
import com.judopay.R;
import com.judopay.RegisterCardActivity;

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
public class RegisterCardTest {

    @Rule
    public ActivityTestRule<RegisterCardActivity> activityTestRule = new ActivityTestRule<>(RegisterCardActivity.class, false, false);

    @Test
    public void shouldBeSuccessfulPaymentWhenValidVisaEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        RegisterCardActivity activity = activityTestRule.launchActivity(intent);
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(activity);
        Espresso.registerIdlingResources(idlingResource);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("125"));

        onView(withId(R.id.button))
                .perform(click());

        onView(withText(R.string.add_card_failed))
                .check(matches(isDisplayed()));

        onView(withText(R.string.please_check_details_try_again))
                .check(matches(isDisplayed()));

        Espresso.unregisterIdlingResources(idlingResource);
    }

}
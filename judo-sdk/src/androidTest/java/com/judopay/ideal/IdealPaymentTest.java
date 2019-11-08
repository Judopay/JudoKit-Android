package com.judopay.ideal;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.judopay.IdealPaymentActivity;
import com.judopay.R;
import com.judopay.ResultTestActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class IdealPaymentTest {
    @Rule
    public ActivityTestRule<ResultTestActivity> activityTestRule = new ActivityTestRule<>(ResultTestActivity.class, false, false);

    @Before
    public void setUp() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), IdealPaymentActivity.class);
        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        activityTestRule.launchActivity(intent);
    }

    @Test
    public void shouldBeDisabledWhenBankNotSelected() {
        onView(withId(R.id.name_edit_text)).perform(typeText("Name Surname"));

        onView(withId(R.id.ideal_payment_button)).check(matches(not(isEnabled())));
    }

    @Test
    public void shouldBeEnabledWhenBankIsSelected() {
        onView(withId(R.id.name_edit_text)).perform(typeText("Name Surname"));
        onView(withId(R.id.bank_spinner)).perform(click());
        onView(withContentDescription(R.string.bank_abn_amro)).perform(click());

        onView(withId(R.id.ideal_payment_button)).check(matches(isEnabled()));
    }
}

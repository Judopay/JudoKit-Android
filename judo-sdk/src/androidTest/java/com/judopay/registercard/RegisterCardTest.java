package com.judopay.registercard;

import android.content.Intent;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.JudoTransactionIdlingResource;
import com.judopay.R;
import com.judopay.ResultTestActivity;
import com.judopay.TestActivityUtil;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class RegisterCardTest {

    @Rule
    public ActivityTestRule<ResultTestActivity> activityTestRule = new ActivityTestRule<>(ResultTestActivity.class, false, false);

    @Test
    public void shouldNotShowAlertWhenCardDeclined() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), RegisterCardActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        RegisterCardActivity registerCardActivity = (RegisterCardActivity) TestActivityUtil.getCurrentActivity();

        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(registerCardActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(ViewMatchers.withId(R.id.cardNumberEditText))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("125"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}

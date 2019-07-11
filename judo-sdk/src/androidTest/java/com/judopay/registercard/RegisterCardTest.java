package com.judopay.registercard;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.JudoTransactionIdlingResource;
import com.judopay.R;
import com.judopay.RegisterCardActivity;
import com.judopay.ResultTestActivity;
import com.judopay.TestActivityUtil;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("125"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}

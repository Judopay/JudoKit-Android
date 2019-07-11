package com.judopay.preauth;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.JudoTransactionIdlingResource;
import com.judopay.PreAuthActivity;
import com.judopay.R;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class SuccessfulPreAuthTest {

    @Rule
    public ActivityTestRule<ResultTestActivity> activityTestRule = new ActivityTestRule<>(ResultTestActivity.class, false, false);

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidVisaEntered() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PreAuthActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PreAuthActivity preAuthActivity = (PreAuthActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(preAuthActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidMaestroEntered() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PreAuthActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PreAuthActivity preAuthActivity = (PreAuthActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(preAuthActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0110"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("789"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidAmexEntered() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PreAuthActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PreAuthActivity preAuthActivity = (PreAuthActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(preAuthActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("3469"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidVisaEnteredAndAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PreAuthActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PreAuthActivity preAuthActivity = (PreAuthActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(preAuthActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("TR148PA"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPreAuthWhenValidMaestroEnteredAndAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PreAuthActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PreAuthActivity preAuthActivity = (PreAuthActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(preAuthActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0110"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("789"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("RG48NL"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidAmexEnteredAndAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PreAuthActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PreAuthActivity preAuthActivity = (PreAuthActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(preAuthActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("3469"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("NW67BB"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}

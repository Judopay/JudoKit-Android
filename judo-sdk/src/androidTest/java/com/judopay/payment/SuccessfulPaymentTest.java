package com.judopay.payment;

import android.content.Intent;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.judopay.Judo;
import com.judopay.JudoTransactionIdlingResource;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.ResultTestActivity;
import com.judopay.TestActivityUtil;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class SuccessfulPaymentTest {

    @Rule
    public ActivityTestRule<ResultTestActivity> activityTestRule = new ActivityTestRule<>(ResultTestActivity.class, false, false);

    @Test
    public void shouldBeSuccessfulPaymentWhenValidVisaEntered() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(ViewMatchers.withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidMaestroEntered() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0110"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("789"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidAmexEntered() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAmexEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("3469"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidVisaEnteredAndAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAmexEnabled(true)
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("TR148PA"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidVisaEnteredAndAvsEnabledWithLowercasePostcode() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAmexEnabled(true)
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("tr14 8pa"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidMaestroEnteredAndAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .setMaestroEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0110"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("789"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("RG48NL"));

        onView(withId(R.id.entryButton))
                .perform(closeSoftKeyboard(), click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulPaymentWhenValidAmexEnteredAndAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .setAmexEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);
        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("3469"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("NW67BB"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}

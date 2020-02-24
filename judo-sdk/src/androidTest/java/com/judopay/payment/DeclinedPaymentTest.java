package com.judopay.payment;

import android.content.Intent;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.judopay.JudoTransactionIdlingResource;
import com.judopay.R;
import com.judopay.ResultTestActivity;
import com.judopay.TestActivityUtil;

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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DeclinedPaymentTest {

    @Rule
    public ActivityTestRule<ResultTestActivity> activityTestRule = new ActivityTestRule<>(ResultTestActivity.class, false, false);

    @Test
    public void shouldDeclineInvalidVisaCard() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();

        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("125"));

        onView(withId(R.id.entryButton))
                .perform(click());

        assertThat(activity, ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED)));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldDeclineInvalidMaestroCard() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();

        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("6759000000009076"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0110"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("671"));

        onView(withId(R.id.entryButton))
                .perform(click());

        assertThat(activity, ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED)));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldDeclineInvalidAmexCard() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();

        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("340000150358074"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("7654"));

        onView(withId(R.id.entryButton))
                .perform(click());

        assertThat(activity, ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED)));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldDeclineInvalidVisaCardWhenAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();

        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("4221690000004963"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("125"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("N226JN"));

        onView(withId(R.id.entryButton))
                .perform(click());

        assertThat(activity, ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED)));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldDeclineInvalidMaestroWhenAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();

        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("6759000000009076"));

        onView(withId(R.id.start_date_edit_text))
                .perform(typeText("0110"));

        onView(withId(R.id.issue_number_edit_text))
                .perform(typeText("01"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("671"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("OX143AL"));

        onView(withId(R.id.entryButton)).perform(closeSoftKeyboard(), click());

        assertThat(activity, ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED)));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldDeclineInvalidAmexWhenAvsEnabled() {
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();

        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.cardNumberEditText))
                .perform(typeText("340000150358074"));

        onView(withId(R.id.expiryDateEditText))
                .perform(typeText("1220"));

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("7654"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("TQ11BH"));

        onView(withId(R.id.entryButton))
                .perform(click());

        assertThat(activity, ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_DECLINED)));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
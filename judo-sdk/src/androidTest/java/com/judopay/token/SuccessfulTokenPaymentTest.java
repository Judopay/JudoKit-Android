package com.judopay.token;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.JudoTransactionIdlingResource;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.ResultTestActivity;
import com.judopay.TestActivityUtil;
import com.judopay.model.Address;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterCardRequest;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import rx.observers.TestSubscriber;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.JUDO_ID;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class SuccessfulTokenPaymentTest {

    @Rule
    public ActivityTestRule<ResultTestActivity> activityTestRule = new ActivityTestRule<>(ResultTestActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        IdlingPolicies.setIdlingResourceTimeout(3, TimeUnit.MINUTES);
    }

    @Test
    public void shouldBeSuccessfulTokenPayment() {
        final JudoApiService apiService = getJudo().getApiService(getContext());

        RegisterCardRequest registerCardRequest = new RegisterCardRequest.Builder()
                .setJudoId(JUDO_ID)
                .setCardNumber("5100000000005460")
                .setExpiryDate("12/20")
                .setCv2("524")
                .setConsumerReference(UUID.randomUUID().toString())
                .build();

        TestSubscriber<Receipt> subscriber = new TestSubscriber<>();

        apiService.registerCard(registerCardRequest)
                .subscribe(subscriber);

        subscriber.assertNoErrors();

        Receipt receipt = subscriber.getOnNextEvents().get(0);
        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setCardToken(receipt.getCardDetails())
                .setConsumerReference(receipt.getConsumer().getYourConsumerReference())
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        Espresso.registerIdlingResources(idlingResource);

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("524"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulTokenPaymentWhenAvsEnabled() {
        final JudoApiService apiService = getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build()
                .getApiService(getContext());

        RegisterCardRequest registerCardRequest = new RegisterCardRequest.Builder()
                .setJudoId(JUDO_ID)
                .setConsumerReference(UUID.randomUUID().toString())
                .setCardNumber("5100000000005460")
                .setExpiryDate("12/20")
                .setCv2("524")
                .setCardAddress(new Address.Builder()
                        .setPostCode("S20 5EJ")
                        .setCountryCode(826)
                        .build())
                .build();

        TestSubscriber<Receipt> subscriber = new TestSubscriber<>();

        apiService.registerCard(registerCardRequest)
                .subscribe(subscriber);

        subscriber.assertNoErrors();

        Receipt receipt = subscriber.getOnNextEvents().get(0);

        Intent subjectIntent = new Intent(getInstrumentation().getTargetContext(), PaymentActivity.class);
        subjectIntent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .setCardToken(receipt.getCardDetails())
                .setConsumerReference(receipt.getConsumer().getYourConsumerReference())
                .build());

        Intent intent = ResultTestActivity.createIntent(subjectIntent);

        ResultTestActivity activity = activityTestRule.launchActivity(intent);

        PaymentActivity paymentActivity = (PaymentActivity) TestActivityUtil.getCurrentActivity();
        JudoTransactionIdlingResource idlingResource = new JudoTransactionIdlingResource(paymentActivity);
        Espresso.registerIdlingResources(idlingResource);

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("524"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("S20 5EJ"));

        onView(withId(R.id.button))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        Espresso.unregisterIdlingResources(idlingResource);
    }

}
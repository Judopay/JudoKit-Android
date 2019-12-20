package com.judopay.token;

import android.content.Intent;

import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

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

import io.reactivex.observers.TestObserver;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.judopay.TestUtil.JUDO_ID_IRIDIUM;
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
        final JudoApiService apiService = getJudo().getApiService(InstrumentationRegistry.getInstrumentation().getTargetContext());

        RegisterCardRequest registerCardRequest = new RegisterCardRequest.Builder()
                .setJudoId(JUDO_ID_IRIDIUM)
                .setCardNumber("5100000000005460")
                .setExpiryDate("12/20")
                .setCv2("524")
                .setConsumerReference(UUID.randomUUID().toString())
                .build();

        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.registerCard(registerCardRequest)
                .subscribe(testObserver);

        testObserver.assertNoErrors();

        Receipt receipt = testObserver.values().get(0);
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
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("524"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void shouldBeSuccessfulTokenPaymentWhenAvsEnabled() {
        final JudoApiService apiService = getJudo()
                .newBuilder()
                .setAvsEnabled(true)
                .build()
                .getApiService(InstrumentationRegistry.getInstrumentation().getTargetContext());

        RegisterCardRequest registerCardRequest = new RegisterCardRequest.Builder()
                .setJudoId(JUDO_ID_IRIDIUM)
                .setConsumerReference(UUID.randomUUID().toString())
                .setCardNumber("5100000000005460")
                .setExpiryDate("12/20")
                .setCv2("524")
                .setCardAddress(new Address.Builder()
                        .setPostCode("S20 5EJ")
                        .setCountryCode(826)
                        .build())
                .build();

        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.registerCard(registerCardRequest)
                .subscribe(testObserver);

        testObserver.assertNoErrors();

        Receipt receipt = testObserver.values().get(0);

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
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("524"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("S20 5EJ"));

        onView(withId(R.id.entryButton))
                .perform(click());

        Matcher<ResultTestActivity> matcher = ResultTestActivity.receivedExpectedResult(equalTo(Judo.RESULT_SUCCESS));
        assertThat(activity, matcher);

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}

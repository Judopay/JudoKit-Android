package com.judopay.paymentmethod;


import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.PaymentMethodActivity;
import com.judopay.R;
import com.judopay.model.PaymentMethod;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.EnumSet;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.init;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.release;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class PaymentMethodTest {

    @Rule
    public ActivityTestRule<PaymentMethodActivity> activityTestRule = new ActivityTestRule<>(PaymentMethodActivity.class, false, false);

    @Test
    public void shouldBeVisibleCreatePaymentButton() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setPaymentMethod(EnumSet.of(PaymentMethod.CREATE_PAYMENT))
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPBBA)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btnGPAY)).check(matches(not(isDisplayed())));
    }

    @Test
    public void shouldBeVisibleCreatePaymentButtonAndPBBAButton() {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setPaymentMethod(EnumSet.of(PaymentMethod.CREATE_PAYMENT, PaymentMethod.PBBA_PAYMENT)).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPBBA)).check(matches(isDisplayed()));
        onView(withId(R.id.btnGPAY)).check(matches(not(isDisplayed())));
    }

    @Test
    public void shouldBeVisibleAllButtons() {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setPaymentMethod(EnumSet.of(PaymentMethod.CREATE_PAYMENT, PaymentMethod.PBBA_PAYMENT, PaymentMethod.GPAY_PAYMENT)).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPBBA)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldBeVisibleAllButtonsWhenPaymentMethodIsEmpty() {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setPaymentMethod(EnumSet.noneOf(PaymentMethod.class)).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPBBA)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldBeVisibleAllButtonsWhenPaymentMethodIsNull() {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPBBA)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldStartCardPaymentFlow() {
        init();
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setPaymentMethod(EnumSet.of(PaymentMethod.CREATE_PAYMENT)).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).perform(click());

        intended(hasComponent(PaymentActivity.class.getName()));
        release();
    }
}

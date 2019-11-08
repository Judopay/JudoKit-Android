package com.judopay.paymentmethod;


import android.content.Intent;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.IdealPaymentActivity;
import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.PaymentMethodActivity;
import com.judopay.R;
import com.judopay.model.PaymentMethod;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.EnumSet;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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
        onView(withId(R.id.btnGPAY)).check(matches(not(isDisplayed())));
    }

    @Test
    public void shouldBeVisibleAllButtonsWhenPaymentMethodIsEmpty() {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setPaymentMethod(EnumSet.noneOf(PaymentMethod.class)).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldBeVisibleAllButtonsWhenPaymentMethodIsNull() {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.btnCardPayment)).check(matches(isDisplayed()));
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

    @Test
    public void shouldBeVisibleIdealPaymentButtonOnIdealEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setIdealEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.ideal_payment_button)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldNotBeVisibleIdealPaymentButtonOnIdealDisabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setIdealEnabled(false)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.ideal_payment_button)).check(matches(not(isDisplayed())));
    }

    @Test
    public void shouldStartIdealPaymentFlowOnIdealButtonClick() {
        init();
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PaymentMethodActivity.class);
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .newBuilder()
                .setIdealEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.ideal_payment_button)).perform(click());

        intended(hasComponent(IdealPaymentActivity.class.getName()));
        release();
    }
}

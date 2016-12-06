package com.judopay.theme;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.RegisterCardActivity;
import com.judopay.model.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.judopay.util.ViewMatchers.withActionBarTitle;


@RunWith(AndroidJUnit4.class)
public class ActivityTitleTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Rule
    public ActivityTestRule<RegisterCardActivity> registerCardTestRule = new ActivityTestRule<>(RegisterCardActivity.class, false, false);

    @Test
    public void shouldDisplayEnterCardDetailsActivityTitle() {
        activityTestRule.launchActivity(getIntent());

        onView(withActionBarTitle(R.string.enter_card_details))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayAddCardActivityTitleWhenRegisterCard() {
        registerCardTestRule.launchActivity(getIntent());

        onView(withActionBarTitle(R.string.add_card))
                .check(matches(isDisplayed()));
    }

    private Intent getIntent() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new Judo.Builder()
                .setEnvironment(Judo.UAT)
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference(UUID.randomUUID().toString())
                .build());

        return intent;
    }

}

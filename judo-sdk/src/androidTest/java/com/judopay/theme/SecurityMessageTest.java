package com.judopay.theme;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PaymentActivity;
import com.judopay.PreAuthActivity;
import com.judopay.R;
import com.judopay.model.Currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.util.ViewMatchers.isNotDisplayed;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SecurityMessageTest {

    @Rule
    public ActivityTestRule<PaymentActivity> testRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Rule
    public ActivityTestRule<PreAuthActivity > preAuthActivityActivityTestRule = new ActivityTestRule<>(PreAuthActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setEnvironment(Judo.UAT);
    }

    @Test
    public void shouldDisplaySecurityMessageWhenSetInTheme() {
        testRule.launchActivity(getIntent());

        onView(withId(R.id.secure_server_text))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldNotDisplaySecurityMessage() {
        preAuthActivityActivityTestRule.launchActivity(getIntent());

        onView(withId(R.id.secure_server_text))
                .check(matches(isNotDisplayed()));
    }

    private Intent getIntent() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef(UUID.randomUUID().toString())
                .build());

        return intent;
    }

}
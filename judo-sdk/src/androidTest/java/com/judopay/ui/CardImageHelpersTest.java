package com.judopay.ui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.model.Currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.ui.util.ViewMatchers.isOpaque;
import static com.judopay.ui.util.ViewMatchers.isTranslucent;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CardImageHelpersTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setEnvironment(Judo.SANDBOX);
    }

    @Test
    public void shouldShowCardTypeImageAsOpaqueAfterCardNumberEntered() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(click());

        onView(withId(R.id.card_type_view))
                .check(matches(isOpaque()));
    }

    @Test
    public void shouldShowSecurityCodeImageAsTranslucentWhenNotFocused() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(click());

        onView(withId(R.id.security_code_image_view))
                .check(matches(isTranslucent()));
    }

    @Test
    public void shouldShowCardImageAsTranslucentAfterDeletedAndNotFocused() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976"))
                .perform(replaceText(""));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(click());

        onView(withId(R.id.card_type_view))
                .check(matches(isTranslucent()));
    }

    private Intent getIntent() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef(UUID.randomUUID().toString())
                .build());
        return intent;
    }

}

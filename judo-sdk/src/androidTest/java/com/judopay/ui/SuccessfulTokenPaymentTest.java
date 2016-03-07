package com.judopay.ui;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.R;
import com.judopay.RegisterCardActivity;
import com.judopay.TokenPaymentActivity;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.ui.util.ActivityUtil.getResultIntent;
import static com.judopay.ui.util.ActivityUtil.resultCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SuccessfulTokenPaymentTest {

    @Rule
    public IntentsTestRule<RegisterCardActivity> registerCardActivityTestRule = new IntentsTestRule<>(RegisterCardActivity.class, false, false);

    @Rule
    public ActivityTestRule<TokenPaymentActivity> tokenPaymentActivityTestRule = new ActivityTestRule<>(TokenPaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setEnvironment(Judo.Environment.SANDBOX);
    }

    @Test
    @Ignore
    public void shouldBeSuccessfulTokenPayment() {
        Judo.setAvsEnabled(false);

        RegisterCardActivity registerCardActivity = registerCardActivityTestRule.launchActivity(getRegisterCardIntent());

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.payment_button))
                .perform(click());

        Intent result = getResultIntent(registerCardActivity);
        Receipt receipt = result.getParcelableExtra(Judo.JUDO_RECEIPT);

        TokenPaymentActivity tokenPaymentActivity = tokenPaymentActivityTestRule.launchActivity(getTokenPaymentIntent(receipt));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(tokenPaymentActivity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    @Ignore
    public void shouldBeSuccessfulTokenPaymentWhenAvsEnabled() {
        Judo.setAvsEnabled(true);
        RegisterCardActivity registerCardActivity = registerCardActivityTestRule.launchActivity(getRegisterCardIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("TR148PA"));

        onView(withId(R.id.payment_button))
                .perform(click());

        Intent result = getResultIntent(registerCardActivity);
        assertThat(resultCode(registerCardActivity), is(Judo.RESULT_SUCCESS));

        Receipt receipt = result.getParcelableExtra(Judo.JUDO_RECEIPT);

        TokenPaymentActivity tokenPaymentActivity = tokenPaymentActivityTestRule.launchActivity(getTokenPaymentIntent(receipt));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("TR148PA"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(tokenPaymentActivity), is(Judo.RESULT_SUCCESS));
    }

    private Intent getTokenPaymentIntent(Receipt receipt) {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setCardToken(receipt.getCardDetails())
                .setConsumerRef(receipt.getConsumer().getYourConsumerReference())
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .build());

        return intent;
    }

    private Intent getRegisterCardIntent() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setConsumerRef("consumerRef")
                .build());

        return intent;
    }

}

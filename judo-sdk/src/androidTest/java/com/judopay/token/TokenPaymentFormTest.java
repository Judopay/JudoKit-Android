package com.judopay.token;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.Judo;
import com.judopay.JudoOptions;
import com.judopay.R;
import com.judopay.TokenPaymentActivity;
import com.judopay.model.CardToken;
import com.judopay.model.CardNetwork;
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
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.model.CardNetwork.AMEX;
import static com.judopay.model.CardNetwork.MAESTRO;
import static com.judopay.model.CardNetwork.VISA;
import static com.judopay.util.ViewMatchers.isDisabled;
import static com.judopay.util.ViewMatchers.isNotDisplayed;
import static com.judopay.util.ViewMatchers.isOpaque;
import static com.judopay.util.ViewMatchers.withTextInputHint;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TokenPaymentFormTest {

    @Rule
    public ActivityTestRule<TokenPaymentActivity> activityTestRule = new ActivityTestRule<>(TokenPaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setEnvironment(Judo.UAT);
    }

    @Test
    public void shouldShowLastFourDigitsWhenVisa() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(VISA));

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .check(matches(withText("**** **** **** 1234")));
    }

    @Test
    public void shouldShowLastFourDigitsWhenAmex() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(AMEX));

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .check(matches(withText("**** ****** *1234")));
    }

    @Test
    public void shouldIgnoreCardNumberIfProvided() {
        Judo.setAvsEnabled(false);

        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCardNumber("9999999999999999")
                .setCurrency(Currency.GBP)
                .setCardNumber("6789")
                .setCardToken(new CardToken("1220", "1234", "cardToken", VISA))
                .setConsumerRef(UUID.randomUUID().toString())
                .build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.card_number_edit_text))
                .check(matches(withText("**** **** **** 1234")));
    }

    @Test
    public void shouldDisplayTokenExpiryDate() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.expiry_date_edit_text))
                .check(matches(withText("12/20")))
                .check(matches(isDisabled()));
    }

    @Test
    public void shouldHaveEmptySecurityCode() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.security_code_edit_text))
                .check(matches(withText("")));
    }

    @Test
    public void shouldDisplayCvvLabelAndHintWhenVisaCardToken() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.security_code_input_layout))
                .check(matches(withTextInputHint("CVV2")));

        onView(withId(R.id.security_code_edit_text))
                .perform(click())
                .check(matches(withHint("000")));
    }

    @Test
    public void shouldDisplayAmexCidvLabelAndHintWhenAmexCardToken() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(AMEX));

        onView(withId(R.id.security_code_input_layout))
                .check(matches(withTextInputHint("CID")));

        onView(withId(R.id.security_code_edit_text))
                .perform(click())
                .check(matches(withHint("0000")));
    }

    @Test
    public void shouldAutoMoveToPostcodeFieldWhenCvvEntered() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldShowCardTypeImageAsOpaque() {
        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.card_type_view))
                .check(matches(isOpaque()));
    }

    @Test
    public void shouldNotEnablePayButtonWhenCv2Deleted() {
        Judo.setAvsEnabled(false);

        Intent intent = getIntent(CardNetwork.VISA);
        activityTestRule.launchActivity(intent);

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("123"))
                .perform(replaceText(""));

        onView(withId(R.id.payment_button))
                .check(matches(isNotDisplayed()));
    }

    @Test
    public void shouldRequireValidCv2WhenVisa() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("12"));

        onView(withId(R.id.payment_button))
                .check(matches(isNotDisplayed()));

        onView(withId(R.id.security_code_edit_text))
                .perform(replaceText("123"));

        onView(withId(R.id.payment_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldRequireValidCidWhenAmex() {
        Judo.setAmexEnabled(true);
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(AMEX));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("123"));

        onView(withId(R.id.payment_button))
                .check(matches(isNotDisplayed()));

        onView(withId(R.id.security_code_edit_text))
                .perform(replaceText("1234"));

        onView(withId(R.id.payment_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowPayButtonWhenAmexCidEnteredAndAvsEnabled() {
        Judo.setAvsEnabled(true);
        Judo.setAmexEnabled(true);

        activityTestRule.launchActivity(getIntent(AMEX));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("1234"));

        onView(withId(R.id.post_code_edit_text))
                .perform(typeText("NW6 7BB"));

        onView(withId(R.id.payment_button))
                .check(matches(isDisplayed()));
    }

    public Intent getIntent(int cardType) {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setCardNumber("6789")
                .setCardToken(new CardToken("1220", "1234", "cardToken", cardType))
                .setConsumerRef(UUID.randomUUID().toString())
                .build());

        return intent;
    }

}
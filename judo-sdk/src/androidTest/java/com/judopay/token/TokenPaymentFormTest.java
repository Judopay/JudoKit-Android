package com.judopay.token;

import android.content.Intent;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.TestUtil;
import com.judopay.model.CardToken;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.model.CardNetwork.AMEX;
import static com.judopay.model.CardNetwork.VISA;
import static com.judopay.util.ViewMatchers.isDisabled;
import static com.judopay.util.ViewMatchers.isNotDisplayed;
import static com.judopay.util.ViewMatchers.isOpaque;
import static com.judopay.util.ViewMatchers.withTextInputHint;

@RunWith(AndroidJUnit4.class)
public class TokenPaymentFormTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldShowLastFourDigitsWhenVisa() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.cardNumberEditText))
                .check(matches(withText("**** **** **** 1234")));
    }

    @Test
    public void shouldShowLastFourDigitsWhenAmex() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(AMEX).build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.cardNumberEditText))
                .check(matches(withText("**** ****** *1234")));
    }

    @Test
    public void shouldIgnoreCardNumberIfProvided() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.cardNumberEditText))
                .check(matches(withText("**** **** **** 1234")));
    }

    @Test
    public void shouldDisplayTokenExpiryDate() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.expiryDateEditText))
                .check(matches(withText("12/20")))
                .check(matches(isDisabled()));
    }

    @Test
    public void shouldHaveEmptySecurityCode() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .check(matches(withText("")));
    }

    @Test
    public void shouldDisplayCvvLabelAndHintWhenVisaCardToken() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .check(matches(withHint("CVV2")));
    }

    @Test
    public void shouldDisplayAmexCidvLabelAndHintWhenAmexCardToken() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(AMEX).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .check(matches(withHint("CID")));
    }

    @Test
    public void shouldAutoMoveToPostcodeFieldWhenCvvEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA)
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("452"));

        onView(withId(R.id.postCodeEditText))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldShowCardTypeImage() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.cardNumberImageView))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void shouldNotEnablePayButtonWhenCv2Deleted() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("123"))
                .perform(replaceText(""));

        onView(withId(R.id.entryButton))
                .check(matches(isDisabled()));
    }

    @Test
    public void shouldRequireValidCv2WhenVisa() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(VISA).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("12"));

        onView(withId(R.id.entryButton))
                .check(matches(isDisabled()));

        onView(withId(R.id.securityCodeEditText))
                .perform(replaceText("123"));

        onView(withId(R.id.entryButton))
                .check(matches(isEnabled()));
    }

    @Test
    public void shouldRequireValidCidWhenAmex() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(AMEX).build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("123"));

        onView(withId(R.id.entryButton))
                .check(matches(isDisabled()));

        onView(withId(R.id.securityCodeEditText))
                .perform(replaceText("1234"));

        onView(withId(R.id.entryButton))
                .check(matches(isEnabled()));
    }

    @Test
    public void shouldShowPayButtonWhenAmexCidEnteredAndAvsEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo(AMEX)
                .setAvsEnabled(true)
                .build());

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.securityCodeEditText))
                .perform(typeText("1234"));

        onView(withId(R.id.postCodeEditText))
                .perform(typeText("NW6 7BB"));

        onView(withId(R.id.entryButton))
                .check(matches(isEnabled()));
    }

    private Judo.Builder getJudo(int cardType) {
        return TestUtil.getJudo()
                .newBuilder()
                .setCardNumber("6789")
                .setCardToken(new CardToken("1220", "1234", "cardToken", cardType, "scheme", "funding", "category", "country", "bank"));
    }
}

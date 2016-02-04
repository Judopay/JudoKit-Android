package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.model.CardToken;
import com.judopay.model.Currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.util.JudoViewMatchers.withTextInputHint;
import static com.judopay.model.CardType.AMEX;
import static com.judopay.model.CardType.VISA;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TokenPaymentFormFieldFormattingTest {

    @Rule
    public ActivityTestRule<TokenPaymentActivity> activityTestRule = new ActivityTestRule<>(TokenPaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setup("fakeApiToken", "fakeApiSecret", Judo.Environment.SANDBOX);
    }

    @Test
    public void shouldDisplayFirst12CardNumberDigitsAsAsterisks() {
        Judo.setAvsEnabled(false);
        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.card_number_edit_text))
                .check(matches(withText("**** **** **** 1234")));
    }

    @Test
    public void shouldDisplayExpiryDateAsAsterisks() {
        Judo.setAvsEnabled(false);
        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.expiry_date_edit_text))
                .check(matches(withText("**/**")));
    }

    @Test
    public void shouldHaveEmptyCvvField() {
        Judo.setAvsEnabled(false);
        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.cvv_edit_text))
                .check(matches(withText("")));
    }

    @Test
    public void shouldDisplayCvvLabelAndHintWhenVisaCardToken() {
        Judo.setAvsEnabled(false);
        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.cvv_input_layout))
                .check(matches(withTextInputHint("CVV")));

        onView(withId(R.id.cvv_edit_text))
                .perform(click())
                .check(matches(withHint("000")));
    }

    @Test
    public void shouldDisplayAmexCidvLabelAndHintWhenAmexCardToken() {
        Judo.setAvsEnabled(false);

        activityTestRule.launchActivity(getIntent(AMEX));

        onView(withId(R.id.cvv_input_layout))
                .check(matches(withTextInputHint("CIDV")));

        onView(withId(R.id.cvv_edit_text))
                .perform(click())
                .check(matches(withHint("0000")));
    }

    @Test
    public void shouldAutoMoveToPostcodeFieldWhenCvvEntered() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent(VISA));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .check(matches(hasFocus()));
    }

    private Intent getIntent(int cardType) {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setCardToken(new CardToken("12/20", "1234", "cardToken", cardType))
                .setConsumerRef("consumerRef")
                .build());

        return intent;
    }

}

package com.judopay;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.judopay.model.Country;
import com.judopay.model.Currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.util.ActivityUtil.resultCode;
import static com.judopay.util.JudoViewMatchers.isDisabled;
import static com.judopay.util.JudoViewMatchers.isNotDisplayed;
import static com.judopay.util.JudoViewMatchers.withTextInputHint;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PaymentFormTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activityTestRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Before
    public void setupJudoSdk() {
        Judo.setup("fakeApiToken", "fakeApiSecret", Judo.Environment.SANDBOX);
    }

    @Test
    public void shouldHaveAmexCardNumberFormattingWhenAmexCardEntered() {
        Judo.setAmexEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"))
                .check(matches(withText("3400 004321 28428")));
    }

    @Test
    public void shouldHaveNormalCardNumberFormattingWhenVisaCardNumberEntered() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"))
                .check(matches(withText("4976 0000 0000 3436")));
    }

    @Test
    public void shouldDisplayCidvHintWhenAmexCardNumberEntered() {
        Judo.setAmexEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.cvv_edit_text))
                .perform(click())
                .check(matches(withHint("0000")));
    }

    @Test
    public void shouldDisplayCv2HintWhenVisaCardNumberEntered() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.cvv_edit_text))
                .perform(click())
                .check(matches(withHint("000")));
    }

    @Test
    public void shouldDisplayCvvLabelWhenVisaCardNumberEntered() {
        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.cvv_input_layout))
                .check(matches(withTextInputHint("CVV")));
    }

    @Test
    public void shouldDisplayCidvLabelWhenAmexCardNumberEntered() {
        Judo.setAmexEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("340000432128428"));

        onView(withId(R.id.cvv_input_layout))
                .check(matches(withTextInputHint("CIDV")));
    }

    @Test
    public void shouldDisablePostcodeFieldWhenOtherCountrySelected() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));
    }

    @Test
    public void shouldAllowPaymentWhenAvsEnabledAndOtherCountrySelected() {
        Judo.setAvsEnabled(true);

        PaymentActivity activity = activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.payment_button))
                .perform(click());

        assertThat(resultCode(activity), is(Judo.RESULT_SUCCESS));
    }

    @Test
    public void shouldNotAllowPaymentWhenAvsEnabledAndPostcodeNotEntered() {
        Judo.setAvsEnabled(true);

        activityTestRule.launchActivity(getIntent());

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.cvv_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .perform(click());

        onView(withText(Country.UNITED_KINGDOM))
                .perform(click());

        onView(withId(R.id.payment_button))
                .check(matches(isNotDisplayed()));
    }

    protected Intent getIntent() {
        Intent intent = new Intent();

        intent.putExtra(Judo.JUDO_OPTIONS, new JudoOptions.Builder()
                .setJudoId("100407196")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .build());
        return intent;
    }

}
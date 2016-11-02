package com.judopay.customlayout;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.model.Currency;
import com.judopay.model.CustomLayout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.judopay.util.ViewMatchers.isNotDisplayed;

@RunWith(AndroidJUnit4.class)
public class CustomLayoutValidationTest {

    @Rule
    public ActivityTestRule<PaymentActivity> testRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldDisplaySubmitButtonWhenFormValid() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        testRule.launchActivity(intent);

        onView(withId(R.id.pay_button))
                .check(matches(isNotDisplayed()));

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.pay_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayCardNumberError() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        testRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("1111111111111111"));

        onView(withText("Check card number"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.pay_button))
                .check(matches(isNotDisplayed()));
    }

    @Test
    public void shouldDisplayExpiryDateError() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        testRule.launchActivity(intent);

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1215"));

        onView(withText("Check expiry date"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.pay_button))
                .check(matches(isNotDisplayed()));
    }

    private Judo.Builder getJudo() {
        return new Judo.Builder()
                .setEnvironment(Judo.UAT)
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setReference(UUID.randomUUID().toString())
                .setCustomLayout(new CustomLayout.Builder()
                        .cardNumberInput(R.id.card_number_input)
                        .expiryDateInput(R.id.expiry_date_input)
                        .securityCodeInput(R.id.security_code_input)
                        .issueNumberInput(R.id.issue_number_input)
                        .startDateInput(R.id.start_date_input)
                        .countrySpinner(R.id.country_spinner)
                        .postcodeInput(R.id.post_code_input)
                        .submitButton(R.id.pay_button)
                        .build(R.layout.custom_layout));
    }

}

package com.judopay.customlayout;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.Judo;
import com.judopay.PaymentActivity;
import com.judopay.R;
import com.judopay.TestUtil;
import com.judopay.model.CustomLayout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CustomLayoutAutoAdvanceFieldsTest {

    @Rule
    public ActivityTestRule<PaymentActivity> testRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldAutoAdvanceFields() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .setAvsEnabled(true)
                .build());

        testRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .check(matches(hasFocus()))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .check(matches(hasFocus()))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .check(matches(hasFocus()))
                .perform(typeText("452"));

        onView(withId(R.id.post_code_edit_text))
                .check(matches(hasFocus()));
    }

    @Test
    public void shouldAutoAdvanceMaestroFields() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        testRule.launchActivity(intent);

        onView(withId(R.id.card_number_edit_text))
                .check(matches(hasFocus()))
                .perform(typeText("6759000000005462"));

        onView(withId(R.id.start_date_edit_text))
                .check(matches(hasFocus()))
                .perform(typeText("0107"));

        onView(withId(R.id.issue_number_edit_text))
                .check(matches(hasFocus()))
                .perform(typeText("01"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .check(matches(hasFocus()))
                .perform(typeText("789"));
    }

    private Judo.Builder getJudo() {
        return new Judo.Builder()
                .setEnvironment(Judo.UAT)
                .setJudoId("100915867")
                .setAmount("0.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference(UUID.randomUUID().toString())
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

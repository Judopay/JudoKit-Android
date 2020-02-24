package com.judopay.customlayout;

import android.content.Intent;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.judopay.R;
import com.judopay.TestUtil;
import com.judopay.model.CustomLayout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.judopay.util.ViewMatchers.isNotDisplayed;


@RunWith(AndroidJUnit4.class)
public class CustomLayoutRevealFieldsTest {

    @Rule
    public ActivityTestRule<PaymentActivity> testRule = new ActivityTestRule<>(PaymentActivity.class, false, false);

    @Test
    public void shouldRevealStartDateAndIssueNumberWhenMaestroEntered() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo().build());

        testRule.launchActivity(intent);

        onView(withId(R.id.start_date_input))
                .check(matches(isNotDisplayed()));

        onView(withId(R.id.issue_number_input))
                .check(matches(isNotDisplayed()));

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("6759000000009076"));

        onView(withId(R.id.start_date_input))
                .check(matches(isDisplayed()));

        onView(withId(R.id.issue_number_input))
                .check(matches(isDisplayed()));
    }

    @Test
    public void shouldRevealBillingCountryAndPostcodeWhenAddressVerificationEnabled() {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_OPTIONS, getJudo()
                .setAvsEnabled(true)
                .build());

        testRule.launchActivity(intent);

        onView(withId(R.id.country_spinner))
                .check(matches(isNotDisplayed()));

        onView(withId(R.id.post_code_input))
                .check(matches(isNotDisplayed()));

        onView(withId(R.id.card_number_edit_text))
                .perform(typeText("4976000000003436"));

        onView(withId(R.id.expiry_date_edit_text))
                .perform(typeText("1220"));

        onView(withId(R.id.security_code_edit_text))
                .perform(typeText("452"));

        onView(withId(R.id.country_spinner))
                .check(matches(isDisplayed()));

        onView(withId(R.id.post_code_input))
                .check(matches(isDisplayed()));
    }

    private Judo.Builder getJudo() {
        return TestUtil.getJudo()
                .newBuilder()
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
package com.judopay.payment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

public class PleaseCheckCardNumberDisplayTest extends PaymentTest {

    public void testGivenIAmOnThePaymentForm() {
        whenIPressPaymentScreenButton();

        whenIEnterACardNumber("1234000000001234");

        onView(withText("Please check number")).check(matches(isDisplayed()));
    }
}

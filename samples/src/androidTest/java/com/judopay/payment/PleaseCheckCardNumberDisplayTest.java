package com.judopay.payment;

import io.appium.droiddriver.UiElement;
import io.appium.droiddriver.finders.By;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class PleaseCheckCardNumberDisplayTest extends PaymentTest {

    public void testGivenIAmOnThePaymentForm() {
        pressPaymentScreenButton();

        enterACardNumber("1234000000001234");

        UiElement errorMessage = find(By.text("Please check number"));

        assertThat(errorMessage, notNullValue());
    }
}

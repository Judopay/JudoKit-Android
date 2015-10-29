package com.judopay.payment;

import io.appium.droiddriver.UiElement;
import io.appium.droiddriver.finders.By;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class PaymentFormNumberFormattingTest extends PaymentTest {

    public void testCardNumberFormattingWhenCardIsAMEX() {
        //Given AMEX is enabled
        pressSettingsButton();

        enableAMEX(true);

        pressSettingsBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter an AMEX card number
        enterACardNumber("340000432128428");

        //Then the result should format as 0000 000000 00000
        UiElement errorMessage = find(By.text("3400 004321 28428"));

        assertThat(errorMessage, notNullValue());
    }

    public void testCardNumberFormattingWhenCardIsNotAMEX() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter a non-AMEX card number
        enterACardNumber("4976000000003436");

        //Then the result should format as 0000 0000 0000 0000
        UiElement errorMessage = find(By.text("4976 0000 0000 3436"));

        assertThat(errorMessage, notNullValue());
    }
}

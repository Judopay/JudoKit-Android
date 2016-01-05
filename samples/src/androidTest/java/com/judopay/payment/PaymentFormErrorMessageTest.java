package com.judopay.payment;


import io.appium.droiddriver.UiElement;
import io.appium.droiddriver.finders.By;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class PaymentFormErrorMessageTest extends PaymentTest {

    public void testErrorMessageDisplaysWhenInvalidCardNumberEntered() {
        //Given I am on the payment screen
        pressPaymentScreenButton();

        //When I enter an invalid card number
        enterACardNumber("1234000000001234");

        //Then I am given an error message
        UiElement errorMessage = find(By.text("Please check number"));

        assertThat(errorMessage, notNullValue());
    }



    public void testErrorMessageDisplaysWhenExpiryDateIsSetInThePast() {
        //Given I am on the payment screen
        pressPaymentScreenButton();

        //When I enter an expiry date before today's date
        enterAnExpiryDate("0101");

        //Then I am given an error message
        UiElement errorMessage = find(By.text("Please check date"));

        assertThat(errorMessage, notNullValue());
    }



    public void testErrorMessageDisplaysWhenStartDateIsSetInTheFuture(){
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        //And Maestro is enabled
        enableMaestro(true);

        pressBackButton();

        //And I am on the payment screen
        pressPaymentScreenButton();

        //And I enter a Maestro card number
        enterACardNumber("6759000000005462");

        //When I enter a start date after today's date
        enterAStartDate("0125");

        //Then I am given an error message
        UiElement errorMessage = find(By.text("Please check date"));

        assertThat(errorMessage, notNullValue());
    }



    public void testErrorMessageDisplaysWhenAMEXCardDetailsEnteredWhenAMEXSupportDisabled() {
        //Given AMEX is disabled
        pressSettingsButton();

        enableAMEX(false);

        pressBackButton();

        //And I am on the payment screen
        pressPaymentScreenButton();

        //When I enter an AMEX card number
        enterACardNumber("3400");

        //Then I am given an error message
        UiElement errorMessage = find(By.text("We don't accept AmEx, please use other cards"));

        assertThat(errorMessage, notNullValue());
    }



    public void testErrorMessageDisplaysWhenMaestroCardDetailsEnteredWhenMaestroSupportDisabled() {
        //Given Maestro is disabled
        pressSettingsButton();

        enableMaestro(false);

        pressBackButton();

        //And I am on the payment screen
        pressPaymentScreenButton();

        //When I enter a Maestro card number
        enterACardNumber("6759");

        //Then I am given an error message
        UiElement errorMessage = find(By.text("We don't accept Maestro, please use other cards"));

        assertThat(errorMessage, notNullValue());
    }


    public void testErrorMessageDisplaysWhenInvalidBillingCountryChosen() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        pressBackButton();

        //And I am on the payment screen
        pressPaymentScreenButton();

        //When I enter card details
        enterCardDetails("4976000000003436", "1220", "452");

        //And select "Other" as billing country
        pressBillingCountry();
        pressOther();

        //Then I am given an error message
        UiElement errorMessage = find(By.text("We only accept cards issued in UK, USA and Canada"));

        assertThat(errorMessage, notNullValue());
    }

}

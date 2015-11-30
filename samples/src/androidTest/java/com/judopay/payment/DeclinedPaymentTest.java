package com.judopay.payment;

import io.appium.droiddriver.finders.By;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DeclinedPaymentTest extends PaymentTest {

    public void testPaymentDeclinedAVSDisabled3DSecureDisabledVisa() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4221690000004963", "1215", "125");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a declined message
        assertDeclinedDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentDeclinedAVSDisabled3DSecureDisabledMaestro() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And Maestro is enabled
        enableMaestro(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("6759000000009076", "0107", "01", "1215", "671");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a declined message
        assertDeclinedDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentDeclinedAVSDisabled3DSecureDisabledAMEX() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And AMEX is enabled
        enableAMEX(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("340000150358074", "1215", "7654");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a declined message
        assertDeclinedDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentDeclinedAVSEnabled3DSecureDisabledVisa() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4221690000004963", "1215", "125", "N226JN");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a declined message
        assertDeclinedDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentDeclinedAVSEnabled3DSecureDisabledMaestro() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        //And Maestro is enabled
        enableMaestro(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("6759000000009076", "0107", "01", "1215", "671", "OX143AL");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a declined message
        assertDeclinedDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentDeclinedAVSEnabled3DSecureDisabledAMEX() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        //And AMEX is enabled
        enableAMEX(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("340000150358074", "1215", "7654", "TQ11BH");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a declined message
        assertDeclinedDialogDisplayed();
        pressDialogPrimaryButton();
    }

    protected void assertDeclinedDialogDisplayed() {
        assertThat(find(By.text("Payment failed")).isVisible(), equalTo(true));
        assertThat(find(By.text("Please check your details and try again")).isVisible(), equalTo(true));
    }

}
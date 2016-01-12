package com.judopay.payment;

public class SuccessfulPaymentTest extends PaymentTest {

    public void testPaymentSuccessfulAVSDisabled3DSecureDisabledVisa() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4976000000003436", "1220", "452");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentSuccessfulAVSDisabled3DSecureDisabledMaestro() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And Maestro is enabled
        enableMaestro(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("6759000000005462", "0107", "01", "1220", "789");
        
        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentSuccessfulAVSDisabled3DSecureDisabledAMEX() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And AMEX is enabled
        enableAMEX(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("340000432128428", "1220", "3469");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentSuccessfulAVSEnabled3DSecureDisabledVisa() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4976000000003436", "1220", "452", "TR148PA");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentSuccessfulAVSEnabled3DSecureDisabledMaestro() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        //And Maestro is enabled
        enableMaestro(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("6759000000005462", "0107", "01", "1220", "789", "RG48NL");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testPaymentSuccessfulAVSEnabled3DSecureDisabledAMEX() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        //And AMEX is enabled
        enableAMEX(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("340000432128428", "1220", "3469", "NW67BB");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

}


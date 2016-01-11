package com.judopay.payment;


public class SuccessfulTokenPaymentTest extends PaymentTest {

    public void testTokenPaymentAVSDisabled3DSecureDisabledSuccessfulVisa() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        pressBackButton();

        //And I have registered a card to the device
        pressAddCardScreenButton();

        enterCardDetails("4976000000003436", "1220", "452");

        pressPayButton();

        //And I am on the token payment screen
        //pressTokenPaymentScreenButton();
        pressYesButton();

        //When I enter my CVV
        enterCVV("452");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

    public void testTokenPaymentAVSEnabled3DSecureDisabledSuccessfulVisa() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        pressBackButton();

        //And I have registered a card to the device
        pressAddCardScreenButton();

        enterCardDetails("4976000000003436", "1220", "452", "TR148PA");

        pressPayButton();

        //And I am on the token payment screen
        //pressTokenPaymentScreenButton();
        pressYesButton();

        //When I enter my card details
        enterCVV("452");
        enterAVS("TR148PA");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        assertPaymentSuccessDialogDisplayed();
        pressDialogPrimaryButton();
    }

}


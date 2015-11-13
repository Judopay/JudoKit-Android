package com.judopay.payment;


import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SuccessfulTokenPaymentTest extends PaymentTest {

    public void testTokenPaymentAVSDisabled3DSecureDisabledSuccessfulVisa(){
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        pressBackButton();

        //And I am on the token payment screen
        pressTokenPaymentScreenButton();

        //When I enter my CVV
        enterCVV("452");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testTokenPaymentAVSEnabled2DSecureDisabledSuccessfulVisa() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        pressBackButton();

        //And I am on the token payment screen
        pressTokenPaymentScreenButton();

        //When I enter my card details
        enterCVV("452");
        enterAVS("TR148PA");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }
}


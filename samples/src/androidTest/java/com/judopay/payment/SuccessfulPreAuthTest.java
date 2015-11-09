package com.judopay.payment;


import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SuccessfulPreAuthTest extends PaymentTest {

    public void testPreAuthSuccessfulAVSDisabled3DSecureDisabledVisa() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        pressBackButton();

        //And I am on a pre-auth pre-auth screen
        pressPreAuthScreenButton();

        //When I enter my card details
        enterCardDetails("4976000000003436", "1215", "452");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testPreAuthSuccessfulAVSDisabled3DSecureDisabledMaestro() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And Maestro is enabled
        enableMaestro(true);

        pressBackButton();

        //And I am on a pre-auth screen
        pressPreAuthScreenButton();

        //When I enter my card details
        enterCardDetails("6759000000005462", "0107", "01", "1215", "789");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testPreAuthSuccessfulAVSDisabled3DSecureDisabledAMEX() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And AMEX is enabled
        enableAMEX(true);

        pressBackButton();

        //And I am on a pre-auth screen
        pressPreAuthScreenButton();

        //When I enter my card details
        enterCardDetails("340000432128428", "1215", "3469");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testPreAuthSuccessfulAVSEnabled3DSecureDisabledVisa() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        pressBackButton();

        //And I am on a pre-auth pre-auth screen
        pressPreAuthScreenButton();

        //When I enter my card details
        enterCardDetails("4976000000003436", "1215", "452", "TR148PA");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testPreAuthSuccessfulAVSEnabled3DSecureDisabledMaestro() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And Maestro is enabled
        enableMaestro(true);

        pressBackButton();

        //And I am on a pre-auth screen
        pressPreAuthScreenButton();

        //When I enter my card details
        enterCardDetails("6759000000005462", "0107", "01", "1215", "789", "RG48NL");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testPreAuthSuccessfulAVSEnabled3DSecureDisabledAMEX() {
        //Given AVS is disabled
        pressSettingsButton();

        enableAVS(false);

        //And AMEX is enabled
        enableAMEX(true);

        pressBackButton();

        //And I am on a pre-auth screen
        pressPreAuthScreenButton();

        //When I enter my card details
        enterCardDetails("340000432128428", "1215", "3469", "NW67BB");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }
}

package com.judopay.payment;


import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SettingsButtonsTest extends PaymentTest {

    public void testAVSSwitch() {
        //Given I am on the settings screen
        pressSettingsButton();

        //When I press the AVS switch
        pressAVSSwitch();

        //Then AVS is switched on
        assertThat(id(R.id.avs_switch).isChecked(), equalTo(true));
    }

    public void testSSLPinningSwitch() {
        //Given I am on the settings screen
        pressSettingsButton();

        //When I press the SSL Pinning switch
        pressSSLPinningSwitch();

        //Then SSL Pinning is switched off
        assertThat(id(R.id.ssl_pinning_switch).isChecked(), equalTo(false));
    }

    public void testMaestroSwitch() {
        //Given I am on the settings screen
        pressSettingsButton();

        //When I press the Maestro Switch
        pressMaestroSwitch(true);

        //Then Maestro is switched on
        assertThat(id(R.id.maestro_switch).isChecked(), equalTo(true));
    }

    public void testAMEXSwitch() {
        //Given I am on the settings screen
        pressSettingsButton();

        //When I press the AMEX Switch
        pressAMEXSwitch();

        //Then AMEX is switched on
        assertThat(id(R.id.amex_switch).isChecked(), equalTo(false));
    }

}

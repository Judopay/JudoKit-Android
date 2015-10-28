package com.judopay.payment;

import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SuccessfulPaymentTest extends PaymentTest {

    public void testAuthSuccessfulNo3DSecureVisa() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4976000000003436", "1215", "452");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureVisaDebit() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4921810000005462", "1215", "441");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureMaestro() {
        //Given Maestro is enabled
        enable("Maestro", true);

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("6759000000005462", "0107", "01", "1215", "789");
        
        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureMasterCard() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("5100000000005460", "1215", "524");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureVisaElectron() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4508750000005461", "1215", "568");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureVisaPurchasing() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("4001658035486204", "1215", "234");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureAMEX() {
        //Given AMEX is enabled
        enable("AMEX", true);

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterCardDetails("340000432128428", "1215", "3469");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }
}


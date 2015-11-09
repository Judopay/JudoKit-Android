package com.judopay.payment;

import com.judopay.samples.R;

import io.appium.droiddriver.UiElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class PaymentFormFieldFormattingTest extends PaymentTest {

    public void testCardNumberFormattingWhenCardIsAMEX() {
        //Given AMEX is enabled
        pressSettingsButton();

        enableAMEX(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter an AMEX card number
        enterACardNumber("340000432128428");

        //Then the result should format as 0000 000000 00000
        UiElement cardNumberField = id(R.id.card_number_edit_text);

        assertThat(cardNumberField.getText(), equalTo("3400 004321 28428"));
    }




    public void testCardNumberFormattingWhenCardIsNotAMEX() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter a non-AMEX card number
        enterACardNumber("4976000000003436");

        //Then the result should format as 0000 0000 0000 0000
        UiElement cardNumberField = id(R.id.card_number_edit_text);

        assertThat(cardNumberField.getText(), equalTo("4976 0000 0000 3436"));
    }




    public void testCVVNumberFormattingWhenCardIsAMEX() {
        //Given AMEX is enabled
        pressSettingsButton();

        enableAMEX(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //And I enter an AMEX card number
        enterACardNumber("340000432128428");

        //And press the CVV field
        pressCVVField();

        //Then the CVV field should format as 0000
        UiElement cVVField = id(R.id.cvv_edit_text);

        assertThat(cVVField.getText(), equalTo("0000"));
    }




    public void testCVVNumberFormattingWhenCardIsNotAMEX() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter a non-AMEX card number
        enterACardNumber("4976000000003436");

        //And press the CVV field
        pressCVVField();

        //Then the CVV field should format as 000
        UiElement cVVField = id(R.id.cvv_edit_text);

        assertThat(cVVField.getText(), equalTo("000"));
    }


    public void testCVVTextFormattingWhenCardIsNotAMEX() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter a non-AMEX card number
        enterACardNumber("4976000000003436");

        //Then the CVV field should display "CVV"
        UiElement cVVInput = id(R.id.cvv_input_layout);

        assertThat(cVVInput.getText(), equalTo("CVV"));
    }

    public void testCVVTextFormattingWhenCardIsAMEX() {
        //Given AMEX is enabled
        pressSettingsButton();

        enableAMEX(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //And I enter an AMEX card number
        enterACardNumber("340000432128428");

        //Then the CVV field should display "CIDV"
        UiElement cVVInput = id(R.id.cvv_input_layout);

        assertThat(cVVInput.getText(), equalTo("CIDV"));
    }

}

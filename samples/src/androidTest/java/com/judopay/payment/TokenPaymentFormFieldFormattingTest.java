package com.judopay.payment;


import com.judopay.samples.R;

import io.appium.droiddriver.UiElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

public class TokenPaymentFormFieldFormattingTest extends PaymentTest {

    public void testFirst12DigitsOf16DigitCardNumberAreAsterisks() {
        //Given I have a 16 digit card saved for a token payment
        //When I am on the token payment screen
        pressTokenPaymentScreenButton();

        //Then the first 12 digits of my card should display as: **** **** ****
        UiElement cardNumberField = id(R.id.card_number_edit_text);

        assertThat(cardNumberField.getText(), startsWith("**** **** ****"));
    }

    public void testExpiryDateDigitsAreAsterisks() {
        //Given I have a card saved for a token payment
        //When I am on the token payment screen
        pressTokenPaymentScreenButton();

        //Then the expiry date should be displayed as: **/**
        UiElement expiryDateField = id(R.id.expiry_date_edit_text);

        assertThat(expiryDateField.getText(), equalTo("**/**"));
    }

    public void testCVVFieldIsEmpty() {
        //Given I have a card saved for a token payment
        //When I am on the token payment screen
        pressTokenPaymentScreenButton();

        //Then the CVV field should be empty
        UiElement cVVField = id(R.id.cvv_edit_text);

        assertThat(cVVField.getText(), nullValue());
    }

    public void testCVVInputDisplaysCVVLabel() {
        //Given I have a card saved for a token payment
        //When I am on the token payment screen
        pressTokenPaymentScreenButton();

        //Then the CVV field should be labeled CVV
        UiElement cVVInputLayout = id(R.id.cvv_input_layout);

        assertThat(cVVInputLayout.getText(), equalTo("CVV"));
    }

}

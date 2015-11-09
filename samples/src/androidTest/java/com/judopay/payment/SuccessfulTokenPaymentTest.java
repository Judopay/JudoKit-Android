package com.judopay.payment;


import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SuccessfulTokenPaymentTest extends PaymentTest {

    public void testTokenPaymentSuccessfulVisa(){
        //Given I am on the token payment screen
        pressTokenPaymentScreenButton();

        //When I enter my CVV
        enterCVV("452");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.settings_menu_item).isVisible(), equalTo(true));
    }
}


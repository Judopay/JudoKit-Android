package com.judopay.payment;

import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SuccessfulPaymentTest extends PaymentTest {

    public void testAuthSuccessfulNo3DSecureVisa() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterVisaCardDetails("4976000000003436", "1215", "452");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.progress_container).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureVisaDebit() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterVisaCardDetails("4921810000005462", "1215", "441");

        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
        //TODO update this to assert "Payment Response Success:" message
        assertThat(id(R.id.progress_container).isVisible(), equalTo(true));
    }

    public void testAuthSuccessfulNo3DSecureMaestro() {
        //Given I am on a payment screen
        pressPaymentScreenButton();

        //When I enter my card details
        enterMaestroCardDetails("6759000000005462", "01/07", "01", "1215", "789");
        
        //And I press the pay button
        pressPayButton();

        //Then I should be given a successful message
    }


}

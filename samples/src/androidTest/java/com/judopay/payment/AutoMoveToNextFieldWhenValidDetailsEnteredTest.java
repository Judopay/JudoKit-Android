package com.judopay.payment;

import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AutoMoveToNextFieldWhenValidDetailsEnteredTest extends PaymentTest {


    /*Visa Tests*/
    public void testVisaMovesToExpiryDateFieldWhenValidCardNumberEntered() {
        //Given I am on the payment screen
        pressPaymentScreenButton();

        //When I enter a valid card number
        enterACardNumber("4976000000003436");

        //Then the expiry date field is in focus
        assertThat(id(R.id.expiry_date_edit_text).isFocused(), equalTo(true));
    }

    public void testVisaMovesToCVVFieldWhenValidExpiryDateEntered() {
        //Given I am on the payment screen
        pressPaymentScreenButton();

        //When I enter a valid expiry date
        enterAnExpiryDate("1215");

        //Then the CVV field is in focus
        assertThat(id(R.id.cvv_edit_text).isFocused(), equalTo(true));
    }

    public void testVisaMovesToPostcodeFieldWhenValidCardDetailsEntered() {
        //Given AVS is enabled
        pressSettingsButton();

        enableAVS(true);

        pressBackButton();

        //And I am on the payment screen
        pressPaymentScreenButton();

        //When I enter valid card details
        enterCardDetails("4976000000003436", "1215", "452");

        //Then the Postcode field is in focus
        assertThat(id(R.id.post_code_edit_text).isFocused(), equalTo(true));
    }





    /*Maestro Tests*/
    public void testMaestroMovesToStartDateFieldWhenValidCardNumberEntered() {
        //Given Maestro is enabled
        pressSettingsButton();

        enableMaestro(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter a valid card number
        enterACardNumber("6759000000005462");

        //Then the start date field is in focus
        assertThat(id(R.id.start_date_edit_text).isFocused(), equalTo(true));
    }

    public void testMaestroMovesToIssueNumberFieldWhenValidStartDateEntered() {
        //Given Maestro is enabled
        pressSettingsButton();

        enableMaestro(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter a valid card number
        enterACardNumber("6759000000005462");

        //And I enter a valid start date
        enterAStartDate("0107");

        //Then the issue number field is in focus
        assertThat(id(R.id.issue_number_edit_text).isFocused(), equalTo(true));
    }

    public void testMaestroMovesToCVVFieldWhenValidExpiryDateEntered() {
        //Given Maestro is enabled
        pressSettingsButton();

        enableMaestro(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter a valid card number
        enterACardNumber("6759000000005462");

        //And I enter a valid expiry date
        enterAnExpiryDate("1215");

        //Then the CVV field is in focus
        assertThat(id(R.id.cvv_edit_text).isFocused(), equalTo(true));
    }

    public void testMaestroMovesToPostcodeFieldWhenValidCardDetailsEntered() {
        //Given Maestro is enabled
        pressSettingsButton();

        enableMaestro(true);

        //And AVS is enabled
        enableAVS(true);

        pressBackButton();

        //And I am on a payment screen
        pressPaymentScreenButton();

        //When I enter valid card details
        enterCardDetails("6759000000005462", "0107", "01", "1215", "789");

        //Then the Postcode field is in focus
        assertThat(id(R.id.post_code_edit_text).isFocused(), equalTo(true));
    }

}

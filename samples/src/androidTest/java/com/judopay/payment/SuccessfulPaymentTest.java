package com.judopay.payment;

import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SuccessfulPaymentTest extends PaymentTest {

    public void testGivenIAmOnAPaymentScreen() {
        whenIPressPaymentScreenButton();

        whenIEnterACardNumber("4976000000003436");

        whenIEnterAnExpiryDate("1215");

        whenIEnterCV2("452");

        whenIPressPayButton();

        assertThat(id(R.id.progress_container).isVisible(), equalTo(true));
    }


}

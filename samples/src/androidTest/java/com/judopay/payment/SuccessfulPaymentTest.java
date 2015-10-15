package com.judopay.payment;

public class SuccessfulPaymentTest extends PaymentTest {

    public void testGivenIAmOnAPaymentScreen() {
        whenIPressPaymentScreenButton();

        whenIEnterACardNumber("4976000000003436");

        whenIEnterAnExpiryDate("12/15");

        whenIEnterCV2("452");

        whenIPressPayButton();
    }
}

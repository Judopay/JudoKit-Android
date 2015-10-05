package com.judopay.payment;

import com.judopay.samples.MainActivity;
import com.judopay.samples.R;
import com.judopay.testing.DroidDriverTest;

import io.appium.droiddriver.UiElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MainActivityTest extends DroidDriverTest<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testMakePaymentButtonText() {
        UiElement paymentButton = id(R.id.payment_button);

        assertThat(paymentButton.getText(), equalTo("Make Payment £9.99"));

        paymentButton.click();
        // todo - verify it opens the payment form
    }

}
package com.judopay.payment;

import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MoveFocusToExpiryDateFieldTest extends PaymentTest {

    public void testGivenIAmOnThePaymentForm() {

        whenIPressPaymentScreenButton();

        whenIEnterACardNumber("4976000000003436");

        assertThat(id(R.id.expiry_date_edit_text).getText(), equalTo("MM/YY"));
    }
}

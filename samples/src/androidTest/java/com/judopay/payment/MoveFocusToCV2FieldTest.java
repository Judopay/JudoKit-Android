package com.judopay.payment;

import com.judopay.samples.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MoveFocusToCV2FieldTest extends PaymentTest {

    public void testGivenIAmOnThePaymentForm() {

        pressPaymentScreenButton();

        enterACardNumber("4976000000003436");

        enterAnExpiryDate("1215");

        assertThat(id(R.id.cvv_edit_text).getText(), equalTo("000"));
    }

}

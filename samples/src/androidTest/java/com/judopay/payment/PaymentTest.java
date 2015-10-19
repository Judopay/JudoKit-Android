package com.judopay.payment;

import com.judopay.DroidDriverTest;
import com.judopay.samples.MainActivity;
import com.judopay.samples.R;

import io.appium.droiddriver.UiElement;

public abstract class PaymentTest extends DroidDriverTest<MainActivity> {

    public PaymentTest() {
        super(MainActivity.class);
    }

    public void whenIEnterAnExpiryDate(String expiryDate) {
        UiElement expiryDateElement = id(R.id.expiry_date_edit_text);
        expiryDateElement.setText(expiryDate);
    }

    protected void whenIPressPaymentScreenButton() {
        UiElement paymentButton = id(R.id.payment_button);
        paymentButton.click();
    }

    protected void whenIPressPayButton() {
        UiElement paybutton = id(R.id.payment_button);
        paybutton.click();
    }

    protected void whenIEnterACardNumber(String cardNo) {
        UiElement cardNumber = id(R.id.card_number_edit_text);
        cardNumber.setText(cardNo);
    }

    protected void whenIEnterCV2(String cv2) {
        UiElement cvelement = id(R.id.cvv_edit_text);
        cvelement.setText(cv2);
    }

}

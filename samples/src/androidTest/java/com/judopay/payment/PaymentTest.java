package com.judopay.payment;

import com.judopay.DroidDriverTest;
import com.judopay.samples.MainActivity;
import com.judopay.samples.R;

import io.appium.droiddriver.UiElement;
import io.appium.droiddriver.finders.By;

public abstract class PaymentTest extends DroidDriverTest<MainActivity> {

    public PaymentTest() {
        super(MainActivity.class);
    }





    /*
        Main screen
     */

    protected void pressSettingsButton() {
        UiElement settingsButton = id(R.id.settings_menu_item);

        settingsButton.click();
    }

    protected void pressPaymentScreenButton() {
        UiElement paymentButton = id(R.id.payment_button);

        paymentButton.click();
    }






    /*
        Settings screen
     */

    protected void pressSwitch(UiElement buttonElement, boolean enable) {
        if(enable){
            if(!buttonElement.isChecked()){
                buttonElement.click();
            }
        } else {
            if(buttonElement.isChecked()){
                buttonElement.click();
            }
        }
    }

    protected void pressSettingsBackButton() {
        UiElement backButton = find(By.className("android.widget.ImageButton"));

        backButton.click();
    }

    protected void enableAVS(boolean enable) {
        UiElement buttonElement = id(R.id.avs_switch);

        pressSwitch(buttonElement, enable);
    }

    protected void enableSSLPinning(boolean enable) {
        UiElement buttonElement = id(R.id.ssl_pinning_switch);

        pressSwitch(buttonElement, enable);
    }

    protected void enableMaestro(boolean enable) {
        UiElement buttonElement = id(R.id.maestro_switch);

        pressSwitch(buttonElement, enable);
    }

    protected void enableAMEX(boolean enable) {
        UiElement buttonElement = id(R.id.amex_switch);

        pressSwitch(buttonElement, enable);
    }








    /*
        Payments screen
     */

    protected void enterCardDetails(String cardNo, String expiryDate, String cvv) {
        enterACardNumber(cardNo);
        enterAnExpiryDate(expiryDate);
        enterCVV(cvv);
    }

    protected void enterCardDetails(String cardNo, String startDate, String issueNo, String expiryDate, String cvv) {
        enterACardNumber(cardNo);
        enterAStartDate(startDate);
        enterAnIssueNumber(issueNo);
        enterAnExpiryDate(expiryDate);
        enterCVV(cvv);
    }

    protected void enterACardNumber(String cardNo) {
        UiElement cardNumber = id(R.id.card_number_edit_text);

        cardNumber.setText(cardNo);
    }

    protected void enterAStartDate(String startDate) {
        UiElement expiryDateElement = id(R.id.start_date_edit_text);

        expiryDateElement.setText(startDate);
    }

    protected void enterAnExpiryDate(String expiryDate) {
        UiElement expiryDateElement = id(R.id.expiry_date_edit_text);

        expiryDateElement.setText(expiryDate);
    }

    protected void enterAnIssueNumber(String issueNo) {
        UiElement issueNumberElement = id(R.id.issue_number_edit_text);

        issueNumberElement.setText(issueNo);
    }

    protected void enterCVV(String cvv) {
        UiElement cvElement = id(R.id.cvv_edit_text);

        cvElement.setText(cvv);
    }

    protected void pressPayButton() {
        UiElement payButton = id(R.id.payment_button);

        payButton.click();
    }

    /*
        Billing country dropdown options.
     */
    protected void pressBillingCountry() {
        UiElement billingElement = find(By.resourceId("android:id/text2"));

        billingElement.click();
    }

    protected void pressUK() {
        UiElement uKElement = find(By.text("UK"));

        uKElement.click();
    }

    protected void pressUSA() {
        UiElement uSAElement = find(By.text("USA"));

        uSAElement.click();
    }

    protected void pressCanada() {
        UiElement canadaElement = find(By.text("Canada"));

        canadaElement.click();
    }

    protected void pressOther() {
        UiElement otherElement = find(By.text("Other"));

        otherElement.click();
    }

}

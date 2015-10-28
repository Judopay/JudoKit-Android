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

    protected void enterVisaCardDetails(String cardNo, String expiryDate, String cv2) {
        enterACardNumber(cardNo);
        enterAnExpiryDate(expiryDate);
        enterCV2(cv2);
    }

    protected void enterMaestroCardDetails(String cardNo, String startDate, String issueNo, String expiryDate, String cv2) {
        enterACardNumber(cardNo);
        enterAStartDate(startDate);
        enterAnIssueNumber(issueNo);
        enterAnExpiryDate(expiryDate);
        enterCV2(cv2);
    }

    private void enterAnIssueNumber(String issueNo) {
        UiElement issueNumberElement = id(R.id.issue_number_edit_text);
        issueNumberElement.setText(issueNo);
    }

    protected void enterAStartDate(String startDate) {
        UiElement expiryDateElement = id(R.id.start_date_edit_text);
        expiryDateElement.setText(startDate);
    }

    protected void enterAnExpiryDate(String expiryDate) {
        UiElement expiryDateElement = id(R.id.expiry_date_edit_text);
        expiryDateElement.setText(expiryDate);
    }

    protected void pressPaymentScreenButton() {
        UiElement paymentButton = id(R.id.payment_button);
        paymentButton.click();
    }

    protected void pressPayButton() {
        UiElement payButton = id(R.id.payment_button);
        payButton.click();
    }

    protected void enterACardNumber(String cardNo) {
        UiElement cardNumber = id(R.id.card_number_edit_text);
        cardNumber.setText(cardNo);
    }

    protected void enterCV2(String cv2) {
        UiElement cvElement = id(R.id.cvv_edit_text);
        cvElement.setText(cv2);
    }

    protected void pressSettingsButton() {
        UiElement settingsButton = id(R.id.settings_menu_item);
        settingsButton.click();
    }

    protected void pressAVSSwitch() {
        UiElement aVSSwitch = id(R.id.avs_switch);
        aVSSwitch.click();
    }

    protected void pressSSLPinningSwitch() {
        UiElement sSLSwitch = id(R.id.ssl_pinning_switch);
        sSLSwitch.click();
    }

    protected void pressMaestroSwitch() {
        UiElement maestroSwitch = id(R.id.maestro_switch);
        maestroSwitch.click();
    }

    protected void pressAMEXSwitch() {
        UiElement aMEXSwitch = id(R.id.amex_switch);
        aMEXSwitch.click();
    }

    protected void pressBackButton() {
        UiElement backButton = find(By.className("android.widget.ImageButton"));
        backButton.click();
    }

}

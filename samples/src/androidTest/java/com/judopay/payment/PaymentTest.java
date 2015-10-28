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

    protected void enable(String button, boolean enable) {
        pressSettingsButton();
        setSettingSwitch(button, enable);
        pressSettingsBackButton();
    }

    protected void setSettingSwitch(String setting, boolean enable) {
        UiElement buttonElement;

        switch(setting.toLowerCase()){
            case "avs":
                buttonElement = id(R.id.avs_switch);
                pressSwitch(enable, buttonElement);
                break;
            case "ssl pinning":
                buttonElement = id(R.id.ssl_pinning_switch);
                pressSwitch(enable, buttonElement);
                break;
            case "maestro":
                buttonElement = id(R.id.maestro_switch);
                pressSwitch(enable, buttonElement);
                break;
            case "amex":
                buttonElement = id(R.id.amex_switch);
                pressSwitch(enable, buttonElement);
                break;
            default:
                break;
        }

    }

    protected void pressSwitch(boolean enable, UiElement buttonElement) {
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

    protected void enterAnIssueNumber(String issueNo) {
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

    protected void enterCVV(String cvv) {
        UiElement cvElement = id(R.id.cvv_edit_text);
        cvElement.setText(cvv);
    }

    protected void pressSettingsButton() {
        UiElement settingsButton = id(R.id.settings_menu_item);
        settingsButton.click();
    }

    protected void pressSettingsBackButton() {
        UiElement backButton = find(By.className("android.widget.ImageButton"));
        backButton.click();
    }

}

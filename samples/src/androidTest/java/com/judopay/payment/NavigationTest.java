package com.judopay.payment;


import com.judopay.samples.R;

import io.appium.droiddriver.UiElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NavigationTest extends PaymentTest {

    public void testBackButtonFunctionalOnPaymentScreen() {
        //Given I am on the payment screen
        pressPaymentScreenButton();

        //When I press the back button
        pressBackButton();

        //Then I return to the main screen
        UiElement settingsButton = id(R.id.settings_menu_item);

        assertThat(settingsButton.isVisible(), equalTo(true));
    }

    public void testBackButtonFunctionalOnPreAuthPaymentScreen() {
        //Given I am on the pre-auth payment screen
        pressPreAuthScreenButton();

        //When I press the back button
        pressBackButton();

        //Then I return to the main screen
        UiElement settingsButton = id(R.id.settings_menu_item);

        assertThat(settingsButton.isVisible(), equalTo(true));
    }

    public void testBackButtonFunctionalOnTokenPaymentScreen() {
        //Given I am on the token payment screen
        pressTokenPaymentScreenButton();

        //When I press the back button
        pressBackButton();

        //Then I return to the main screen
        UiElement settingsButton = id(R.id.settings_menu_item);

        assertThat(settingsButton.isVisible(), equalTo(true));
    }
}

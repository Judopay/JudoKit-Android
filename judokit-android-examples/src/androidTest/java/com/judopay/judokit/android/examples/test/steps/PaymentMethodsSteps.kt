package com.judopay.judokit.android.examples.test.steps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.judopay.judokit.android.examples.R
import com.judopay.judokit.android.examples.test.robots.ConfigurationRobot.Companion.scenarioData
import io.cucumber.java.en.Then

class PaymentMethodsSteps {
    @Then("^a (.*?) \"PAYMENT METHODS SUBTITLE\" item should be selected")
    fun isAddedCardSelected(cardNetwork: String) {
        val subtitle =
            scenarioData?.cards?.find { it.cardType == cardNetwork }?.paymentMethodsSubtitle
        onView(withText(subtitle)).check(matches(isDisplayed()))
    }

    @Then("^the payment method selector should not be visible$")
    fun paymentMethodSelectorNotVisible() {
        onView(withId(R.id.slider)).check(doesNotExist())
    }
}

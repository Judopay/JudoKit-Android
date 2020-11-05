package com.judokit.android.examples.test.steps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.judokit.android.examples.R
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.test.espresso.clearData
import com.judokit.android.examples.test.espresso.waitUntilVisible
import com.judokit.android.examples.test.robots.ConfigurationRobot
import io.cucumber.core.api.Scenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not

class PaymentMethodsSteps {

    private lateinit var activityScenario: ActivityScenario<DemoFeatureListActivity>
    private val configurationRobot = ConfigurationRobot()

    @Before("@payment-methods")
    fun setUp(scenario: Scenario) {
        activityScenario = launchActivity()

        val tags = scenario.sourceTagNames
        configurationRobot.configure(tags)
    }

    @After("@payment-methods")
    fun tearDown() {
        activityScenario.close()
        clearData()
    }

    @Then("^the card ending with \"(.*?)\" should be selected$")
    fun isAddedCardSelected(cardEnding: String) {
        onView(
            allOf(
                withId(R.id.radioIconImageView),
                withTagValue(`is`("true")),
                hasSibling(withText("Visa Ending $cardEnding"))
            )
        ).waitUntilVisible().check(
            matches(isDisplayed())
        )
    }

    @Then("^(.*?) payment method should be (?:visible|invisible)$")
    fun paymentMethodVisibility(paymentMethod: String) {
        try {
            val matchedView = onView(withTagValue(`is`(paymentMethod)))
            matchedView.check(matches((isDisplayed())))
        } catch (e: NoMatchingViewException) {
        }
    }

    @Then("^the payment method selector should be invisible$")
    fun paymentMethodSelectorInvisible() {
        try {
            onView(withId(R.id.slider)).check(matches((not(isDisplayed()))))
        } catch (e: NoMatchingViewException) {
        }
    }
}

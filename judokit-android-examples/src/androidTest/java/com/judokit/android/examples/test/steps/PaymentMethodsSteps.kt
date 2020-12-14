package com.judokit.android.examples.test.steps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.judokit.android.examples.R
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.test.espresso.RecyclerViewMatcher
import com.judokit.android.examples.test.espresso.clearData
import com.judokit.android.examples.test.espresso.waitUntilVisible
import com.judokit.android.examples.test.model.TestConfiguration
import com.judokit.android.examples.test.robots.ConfigurationRobot
import io.cucumber.core.api.Scenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf

class PaymentMethodsSteps {

    private lateinit var activityScenario: ActivityScenario<DemoFeatureListActivity>
    private val configurationRobot = ConfigurationRobot()

    @Before("@payment-methods")
    fun setUp(scenario: Scenario) {
        activityScenario = launchActivity()

        val tags = scenario.sourceTagNames
        val jsonString = InstrumentationRegistry.getInstrumentation().context.resources.assets.open(
            "test-input-data.json"
        ).bufferedReader().use { it.readText() }
        val testConfiguration = Gson().fromJson(jsonString, TestConfiguration::class.java)

        configurationRobot.configure(tags, testConfiguration!!)
    }

    @After("@payment-methods")
    fun tearDown() {
        activityScenario.close()
        clearData()
    }

    @Then("^the item nr. (.*?) containing \"(.*?)\" should be selected$")
    fun isAddedCardSelected(position: Int, cardEnding: String) {
        Thread.sleep(2000)
        onView(RecyclerViewMatcher(R.id.recyclerView).atPosition(position)).waitUntilVisible()
            .check(
                matches(
                    hasDescendant(
                        allOf(
                            withId(R.id.radioIconImageView),
                            withTagValue(`is`("true")),
                            hasSibling(withText(cardEnding))
                        )
                    )
                )
            )
    }

    @Then("^the payment method selector should not be visible$")
    fun paymentMethodSelectorNotVisible() {
        onView(withId(R.id.slider)).check(doesNotExist())
    }
}

package com.judokit.android.examples.test.steps

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.judokit.android.JudoActivity
import com.judokit.android.examples.R
import com.judokit.android.examples.SplashActivity
import com.judokit.android.examples.model.DemoFeature
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not

class CardEntrySteps {

    private lateinit var scenario: ActivityScenario<JudoActivity>

    @Before
    fun setUp() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SplashActivity::class.java)
        scenario = launchActivity(intent)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Given("^judo configuration object is set up")
    fun judoSetUp() {
        onView(withId(R.id.action_settings)).perform(click())
        onView(withText(R.string.judo_id_title)).perform(click())
        try {
            onView(withText("judo-id")).perform(replaceText("111111111"))
            onView(withText("OK")).perform(click())
            pressBack()
        } catch (e: Exception) {
            onView(withText("OK")).perform(click())
            pressBack()
        }
    }

    @And("^I am on card entry screen$")
    fun onCardEntry() {
        onView(withText(DemoFeature.PAYMENT.title)).perform(click())
    }

    @When("^I click card number field$")
    fun clickCardNumberField() {
        onView(withId(R.id.numberTextInputEditText)).perform(click())
    }

    @And("^I type in a valid card$")
    fun typeInAValidCard() {
        onView(withId(R.id.numberTextInputEditText)).perform(typeText("4111111111111111"))
    }

    @And("^I type in an invalid card$")
    fun typeInAnInValidCard() {
        onView(withId(R.id.numberTextInputEditText)).perform(typeText("4000000000000000"))
    }

    @Then("^no validation errors should appear$")
    fun noValidationError() {
        onView(
            allOf(
                withId(R.id.errorTextView),
                isDescendantOfA(withId(R.id.numberTextInputLayout))
            )
        ).check(matches(not(isDisplayed())))
    }

    @Then("^validation error should appear$")
    fun validationErrorAppears() {
        onView(
            allOf(
                withId(R.id.errorTextView),
                isDescendantOfA(withId(R.id.numberTextInputLayout))
            )
        ).check(matches(isDisplayed()))
    }
}

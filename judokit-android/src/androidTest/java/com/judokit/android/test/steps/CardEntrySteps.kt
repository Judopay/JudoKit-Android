package com.judokit.android.test.steps

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.judokit.android.JUDO_OPTIONS
import com.judokit.android.Judo
import com.judokit.android.JudoActivity
import com.judokit.android.R
import com.judokit.android.api.model.BasicAuthorization
import com.judokit.android.model.Amount
import com.judokit.android.model.Currency
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.Reference
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not

class CardEntrySteps {

    lateinit var scenario: ActivityScenario<JudoActivity>

    @Before
    fun setUp() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), JudoActivity::class.java)
            .putExtra(JUDO_OPTIONS, getJudo())
        scenario = launchActivity(intent)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Given("^I am on card entry screen$")
    fun onCardEntry() {
        onView(withId(R.id.numberTextInputEditText)).check(matches(isDisplayed()))
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
        onView(allOf(withId(R.id.errorTextView), isDescendantOfA(withId(R.id.numberTextInputLayout)))).check(matches(not(isDisplayed())))
    }

    @Then("^validation error should appear$")
    fun validationErrorAppears() {
        onView(allOf(withId(R.id.errorTextView), isDescendantOfA(withId(R.id.numberTextInputLayout)))).check(matches(isDisplayed()))
    }


    private fun getJudo() = Judo.Builder(PaymentWidgetType.CARD_PAYMENT)
        .setJudoId("111111111")
        .setAuthorization(
            BasicAuthorization.Builder().setApiToken("token").setApiSecret("secret").build()
        )
        .setAmount(Amount("1", Currency.EUR))
        .setReference(Reference("consumer", "payment"))
        .build()
}

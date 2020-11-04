package com.judokit.android.examples.test.steps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.test.espresso.DEFAULT_TIMEOUT
import com.judokit.android.examples.test.espresso.clearData
import com.judokit.android.examples.test.robots.CardEntryRobot
import com.judokit.android.examples.test.robots.ConfigurationRobot
import com.judokit.android.examples.test.robots.ThreeDSecureRobot
import com.judokit.android.model.PaymentWidgetType
import io.cucumber.core.api.Scenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.When

class ThreeDSecureSteps {

    private lateinit var activityScenario: ActivityScenario<DemoFeatureListActivity>
    private val configurationRobot = ConfigurationRobot()

    private val robot = ThreeDSecureRobot()
    private val cardEntryRobot = CardEntryRobot()

    @Before("@3DS")
    fun setUp(scenario: Scenario) {
        activityScenario = launchActivity()
        val tags = scenario.sourceTagNames
        configurationRobot.configure(tags)
    }

    @After("@3DS")
    fun tearDown() {
        activityScenario.close()
        clearData()
    }

    @Given("^I initiated a 3DS (.*?) transaction$")
    fun initiate3DSTransaction(paymentWidgetType: String) {
        cardEntryRobot.isOnScreen("Main")

        when (paymentWidgetType) {
            PaymentWidgetType.CARD_PAYMENT.name ->
                cardEntryRobot.press("Pay with card")
            PaymentWidgetType.PRE_AUTH.name ->
                cardEntryRobot.press("Pre-auth with card")
            PaymentWidgetType.REGISTER_CARD.name ->
                cardEntryRobot.press("Register card")
            PaymentWidgetType.PAYMENT_METHODS.name ->
                cardEntryRobot.press("Payment methods")
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS.name ->
                cardEntryRobot.press("Pre-auth payment methods")
        }

        if (paymentWidgetType == PaymentWidgetType.PAYMENT_METHODS.name ||
            paymentWidgetType == PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS.name
        ) {
            cardEntryRobot.press("ADD CARD")
        }

        cardEntryRobot.enterTextIntoField("4000 0000 0000 0002", "Card Number")
        cardEntryRobot.enterTextIntoField("John Rambo", "Cardholder Name")
        cardEntryRobot.enterTextIntoField("12/29", "Expiry Date")
        cardEntryRobot.enterTextIntoField("123", "Secure Code")

        if (paymentWidgetType == PaymentWidgetType.PAYMENT_METHODS.name ||
            paymentWidgetType == PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS.name
        ) {
            cardEntryRobot.press("ADD CARD")
        }

        when (paymentWidgetType) {
            PaymentWidgetType.REGISTER_CARD.name -> cardEntryRobot.press("REGISTER CARD")
            PaymentWidgetType.CARD_PAYMENT.name, PaymentWidgetType.PRE_AUTH.name -> cardEntryRobot.press(
                "PAY NOW"
            )
            else -> {
                cardEntryRobot.press("PAY NOW")
                cardEntryRobot.enterTextIntoField("123", "Secure Code")
                cardEntryRobot.press("PAY NOW")
            }
        }
        Thread.sleep(DEFAULT_TIMEOUT)
    }

    @When("^I enter \"(.*?)\" in the (.*?) text field on the web page$")
    fun enterText(secureCode: String, field: String) {
        robot.enterTextIntoField(secureCode, field)
    }

    @And("^I tap on \"(.*?)\" button on the web page$")
    fun tapOn(button: String) {
        robot.tapOn(button)
    }
}

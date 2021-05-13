package com.judokit.android.examples.test.steps

import com.judokit.android.examples.test.robots.CardEntryRobot
import com.judokit.android.examples.test.robots.ConfigurationRobot.Companion.scenarioData
import com.judokit.android.examples.test.robots.ConfigurationRobot.Companion.testConfiguration
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.util.concurrent.TimeUnit

class CardEntrySteps {

    private val robot = CardEntryRobot()

    @Given("^I am on the (.*?) (?:screen|view|page)$")
    fun onScreen(screen: String) {
        robot.isOnScreen(screen)
    }

    @When("^I (?:tap|press) on the \"(.*?)\" button$")
    fun pressButton(button: String) {
        robot.press(button)
    }

    @And("^I tap on the \"(.*?)\" (option|cell|item|text field)$")
    fun tapOn(text: String, type: String) {
        robot.tapOn(text, type)
    }

    @And("^I tap on the (.*?) text field$")
    fun tapOnCountryTextField(text: String) {
        robot.tapOn(text, "text field")
    }

    @And("^I enter (.*?) \"(.*?)\" (?:in|into) the (.*?) (?:text|input) field$")
    fun enterTextInto(identifier: String, key: String, fieldName: String) {
        val defaultCard = testConfiguration.defaultCards.find { it.cardType == identifier }
        val card = scenarioData?.cards?.find { it.cardType == identifier }

        when (key) {
            "CARD NUMBER" -> robot.enterTextIntoField(
                card?.cardNumber ?: defaultCard?.cardNumber,
                fieldName
            )
            "CARDHOLDER NAME" -> robot.enterTextIntoField(
                card?.cardHolder ?: defaultCard?.cardHolder, fieldName
            )
            "EXPIRY DATE" -> robot.enterTextIntoField(
                card?.expiryDate ?: defaultCard?.expiryDate,
                fieldName
            )
            "SECURE CODE" -> robot.enterTextIntoField(
                card?.securityCode ?: defaultCard?.securityCode, fieldName
            )
            "POST CODE" -> {
                val avs = scenarioData?.avs?.find { it.country == identifier }
                robot.enterTextIntoField(avs?.postCode, fieldName)
            }
        }
    }

    @And("^I enter \"(.*?)\" (?:in|into) the (.*?) (?:text|input) field$")
    fun enterTextInto(dataToType: String, fieldName: String) {
        robot.enterTextIntoField(dataToType, fieldName)
    }

    @And("^I wait for \"(.*)\" seconds$")
    fun waitFor(timeout: Long) {
        TimeUnit.SECONDS.sleep(timeout)
    }

    @Then("^the (.*?) (screen|page|view|option) should (be|not be) visible$")
    fun viewShouldBeVisible(identifier: String, type: String, visibility: String) {
        robot.isVisible(identifier, type, visibility)
    }

    @Then("^(?:the|an) (.*?) \"(.*?)\" label should (not be|be) visible$")
    fun labelShouldBeVisible(cardNetwork: String, label: String, visibility: String) {
        val card = scenarioData?.cards?.find { it.cardType == cardNetwork }
        val defaultCard = testConfiguration.defaultCards.find { it.cardType == cardNetwork }
        when (label) {
            "SECURE CODE ERROR MESSAGE" -> robot.isVisible(
                card?.secureCodeErrorMessage ?: defaultCard?.secureCodeErrorMessage,
                card?.secureCodeErrorMessage ?: defaultCard?.secureCodeErrorMessage,
                visibility
            )
        }
    }

    @Then("^(?:the|an) \"(.*?)\" label should (not be|be) visible$")
    fun labelShouldBeVisible(label: String, visibility: String) {
        robot.isVisible(label, label, visibility)
    }

    @Then("^the \"(.*?)\" (?:button|option) should be disabled$")
    fun shouldBeDisabled(button: String) {
        robot.isDisabled(button)
    }

    @And("^the \"(.*?)\" (?:list|table) (?:item|option|cell) should contain \"(.*?)\"$")
    fun isValueOfFieldEqual(title: String, value: String) {
        robot.isValueOfFieldEqual(title, value)
    }

    @And("^I select \"(.*?)\" from the Country dropdown$")
    fun selectFromDropdown(country: String) {
        val avs = scenarioData?.avs?.find { it.country == country }!!
        robot.selectFromDropdown(avs.country)
    }

    @Then("^the value in (.*?) (?:text|input) field should be (.*?)$")
    fun expectedInput(textField: String, expectedValue: String) {
        when (expectedValue) {
            "EXPECTED POST CODE" -> robot.expectedValue(textField, expectedValue)
        }
    }

    @Then("^the (.*?) button title should be \"(.*?)\"$")
    fun compareLabel(view: String, label: String) {
        robot.expectedValue(view, label)
    }
}

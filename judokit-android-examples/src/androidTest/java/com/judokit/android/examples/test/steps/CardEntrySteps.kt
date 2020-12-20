package com.judokit.android.examples.test.steps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.test.espresso.clearData
import com.judokit.android.examples.test.model.TestConfiguration
import com.judokit.android.examples.test.model.TestData
import com.judokit.android.examples.test.robots.CardEntryRobot
import com.judokit.android.examples.test.robots.ConfigurationRobot
import io.cucumber.core.api.Scenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Assume

class CardEntrySteps {

    private lateinit var activityScenario: ActivityScenario<DemoFeatureListActivity>
    private val configurationRobot = ConfigurationRobot()
    private val robot = CardEntryRobot()

    private var scenarioData: TestData? = null
    private lateinit var testConfiguration: TestConfiguration

    @Before("@card-entry")
    fun setUp(scenario: Scenario) {
        activityScenario = launchActivity()

        val tags = scenario.sourceTagNames

        val jsonString = InstrumentationRegistry.getInstrumentation().context.resources.assets.open(
            "test-input-data.json"
        ).bufferedReader().use { it.readText() }

        // Parses test-input-data.json into TestConfiguration object
        testConfiguration = Gson().fromJson(jsonString, TestConfiguration::class.java)

        // Skips scenario
        testConfiguration.testsToSkip.forEach {
            if ("@$it" in tags) {
                Assume.assumeTrue(false)
            }
        }

        // Skip scenarios that are not in testsToInclude
        testConfiguration.testsToInclude.forEach {
            if ("@$it" !in tags) {
                Assume.assumeTrue(false)
            }
        }

        // Extract data for the specific scenario by tag
        scenarioData =
            testConfiguration.testData.find { testData -> testData.tags.any { "@$it" in tags } }

        // Apply configurations
        configurationRobot.configure(tags, testConfiguration)
    }

    @After("@card-entry")
    fun tearDown() {
        activityScenario.close()
        clearData()
    }

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
        // no-op
    }

    @Then("^the (.*?) (screen|page|view|item) should (be|not be) visible$")
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

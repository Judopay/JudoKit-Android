package com.judokit.android.examples.test.steps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.test.espresso.clearData
import com.judokit.android.examples.test.robots.CardEntryRobot
import com.judokit.android.examples.test.robots.ConfigurationRobot
import io.cucumber.core.api.Scenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class CardEntrySteps {

    private lateinit var activityScenario: ActivityScenario<DemoFeatureListActivity>
    private val configurationRobot = ConfigurationRobot()
    private val robot = CardEntryRobot()

    @Before("@card-entry")
    fun setUp(scenario: Scenario) {
        activityScenario = launchActivity()

        val tags = scenario.sourceTagNames
        configurationRobot.configure(tags)
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

    @And("^I enter \"(.*?)\" (?:in|into) the (.*?) (?:text|input) field$")
    fun enterTextInto(textToEnter: String, fieldName: String) {
        robot.enterTextIntoField(textToEnter, fieldName)
    }

    @And("^I wait for \"(.*)\" seconds$")
    fun waitFor(timeout: Long) {
        // no-op
    }

    @Then("^the (.*?) (screen|page|view|item) should (be|not be) visible$")
    fun viewShouldBeVisible(identifier: String, type: String, visibility: String) {
        robot.isVisible(identifier, type, visibility)
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
        robot.selectFromDropdown(country)
    }

    @Then("^the value in (.*?) (?:text|input) field should be \"(.*?)\"$")
    fun expectedInput(textField: String, expectedValue: String) {
        robot.expectedValue(textField, expectedValue)
    }

    @Then("^the (.*?) button title should be \"(.*?)\"$")
    fun compareLabel(view: String, label: String) {
        robot.expectedValue(view, label)
    }
}

package com.judokit.android.examples.test.steps

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import com.judokit.android.JudoActivity
import com.judokit.android.examples.SplashActivity
import com.judokit.android.examples.test.robots.Robot
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class CardEntrySteps {

    private lateinit var scenario: ActivityScenario<JudoActivity>
    private val robot = Robot()

    @Before
    fun setUp() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SplashActivity::class.java)
        scenario = launchActivity(intent)
        robot.configureJudo()
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Given("^I am on the (.*?) (?:screen|view|page)$")
    fun mainScreen(screen: String) {
        robot.isOnScreen(screen)
    }

    @When("^I (?:tap|press) on the \"(.*?)\" button$")
    fun pressSettingsButton(button: String) {
        robot.press(button)
    }

    @When("^I tap on the \"(.*?)\" (?:option|cell|item)$")
    fun tapOn(text: String) {
        robot.tapOnText(text)
    }

    @When("^I enter \"(.*?)\" (?:in|into) the (.*?) (?:text|input) field$")
    fun enterTextInto(textToEnter: String, fieldName: String) {
        robot.enterTextIntoField(textToEnter, fieldName)
    }

    @And("^I wait for \"(.*)\" seconds$")
    fun waitFor(timeout: Long) {
        // no-op
    }

    @Then("^the (.*?) (?:screen|page|view) should be visible$")
    fun shouldBeVisible(screen: String) {
        robot.isVisible(screen)
    }
}

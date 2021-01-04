package com.judokit.android.examples.test.steps

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.test.espresso.clearData
import com.judokit.android.examples.test.model.TestConfiguration
import com.judokit.android.examples.test.robots.ConfigurationRobot
import io.cucumber.core.api.Scenario
import io.cucumber.java.After
import io.cucumber.java.Before
import org.junit.Assume

class ConfigurationSteps {
    private lateinit var activityScenario: ActivityScenario<DemoFeatureListActivity>
    private val configurationRobot = ConfigurationRobot()

    @Before("@card-entry, @payment-methods, @3DS")
    fun setUp(scenario: Scenario) {
        activityScenario = launchActivity()

        val tags = scenario.sourceTagNames

        val jsonString = InstrumentationRegistry.getInstrumentation().context.resources.assets.open(
            "test-input-data.json"
        ).bufferedReader().use { it.readText() }

        // Parses test-input-data.json into TestConfiguration object
        ConfigurationRobot.testConfiguration = Gson().fromJson(jsonString, TestConfiguration::class.java)

        // Skips scenario
        ConfigurationRobot.testConfiguration.testsToSkip.forEach {
            if ("@$it" in tags) {
                Assume.assumeTrue(false)
            }
        }

        // Skip scenarios that are not in testsToInclude
        ConfigurationRobot.testConfiguration.testsToInclude.forEach {
            if ("@$it" !in tags) {
                Assume.assumeTrue(false)
            }
        }

        // Extract data for the specific scenario by tag
        ConfigurationRobot.scenarioData =
            ConfigurationRobot.testConfiguration.testData.find { testData -> testData.tags.any { "@$it" in tags } }

        // Apply configurations
        configurationRobot.configure(tags, ConfigurationRobot.testConfiguration)
    }

    @After("@card-entry, @payment-methods, @3DS")
    fun tearDown() {
        activityScenario.close()
        clearData()
    }
}

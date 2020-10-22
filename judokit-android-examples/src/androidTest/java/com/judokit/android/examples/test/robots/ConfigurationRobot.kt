package com.judokit.android.examples.test.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.judokit.android.examples.R
import java.util.Properties

private const val TAG_REQUIRE_NON_3DS_CONFIG = "@require-non-3ds-config"

private const val CREDENTIALS_FILE_NAME = "test-credentials.properties"
private const val JUDO_ID = "judo-id"
private const val TOKEN = "token"
private const val SECRET = "secret"

class ConfigurationRobot {
    private val props = Properties().apply {
        load(
            InstrumentationRegistry.getInstrumentation().context.resources.assets.open(
                CREDENTIALS_FILE_NAME
            )
        )
    }

    fun configure(tags: MutableCollection<String>) {
        onView(withId(R.id.action_settings)).perform(click())
        when {
            tags.contains(TAG_REQUIRE_NON_3DS_CONFIG) -> {
                setJudoId()
                setToken()
                setSecret()
            }
            else -> setJudoId()
        }
        pressBack()
    }

    private fun setJudoId() {
        val judoId = props.getProperty(JUDO_ID)
        onView(withText(R.string.judo_id_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setToken() {
        val judoId = props.getProperty(TOKEN)
        onView(withText(R.string.token_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setSecret() {
        val judoId = props.getProperty(SECRET)
        onView(withText(R.string.secret_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }
}

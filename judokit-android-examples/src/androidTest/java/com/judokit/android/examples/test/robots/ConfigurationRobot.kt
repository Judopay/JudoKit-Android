package com.judokit.android.examples.test.robots

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.judokit.android.examples.R
import java.util.Properties

private const val TAG_REQUIRE_3DS = "@require-3ds-config"
private const val TAG_REQUIRE_NON_3DS = "@require-non-3ds-config"
private const val TAG_REQUIRE_AVS = "@require-avs"
private const val TAG_DISPLAY_AMOUNT_ON_BUTTON = "@display-amount-on-button"

private const val CREDENTIALS_FILE_NAME = "test-credentials.properties"
private const val JUDO_ID = "judo-id"
private const val TOKEN = "token"
private const val THREE_DS_TOKEN = "3DS-token"
private const val SECRET = "secret"
private const val THREE_DS_SECRET = "3DS-secret"

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

        setJudoId()
        for (tag in tags) {
            when (tag) {
                TAG_REQUIRE_NON_3DS -> {
                    setToken(threeDSecureEnabled = false)
                    setSecret(threeDSecureEnabled = false)
                }
                TAG_REQUIRE_3DS -> {
                    setToken(threeDSecureEnabled = true)
                    setSecret(threeDSecureEnabled = true)
                }
                TAG_REQUIRE_AVS -> setAvsEnabled()
                TAG_DISPLAY_AMOUNT_ON_BUTTON -> setDisplayAmountOnButton()
            }
        }
        pressBack()
    }

    private fun setJudoId() {
        val judoId = props.getProperty(JUDO_ID)
        onView(withText(R.string.judo_id_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setToken(threeDSecureEnabled: Boolean) {
        val judoId = props.getProperty(if (threeDSecureEnabled) THREE_DS_TOKEN else TOKEN)
        onView(withText(R.string.token_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setSecret(threeDSecureEnabled: Boolean) {
        val judoId = props.getProperty(if (threeDSecureEnabled) THREE_DS_SECRET else SECRET)
        onView(withText(R.string.secret_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(judoId))
        onView(withText("OK")).perform(click())
    }

    private fun setAvsEnabled() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.avs_title)),
                    click()
                )
            )
    }

    private fun setDisplayAmountOnButton() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.should_payment_button_display_amount_title)),
                    click()
                )
            )
    }
}

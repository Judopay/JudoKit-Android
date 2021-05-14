package com.judokit.android.examples.test.espresso

import android.content.Context
import android.view.View
import android.widget.Checkable
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.platform.app.InstrumentationRegistry
import com.judopay.judokit.android.db.JudoRoomDatabase
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Description
import java.io.File
import java.util.concurrent.TimeoutException

const val TIMEOUT_INTERVAL = 500L
const val DEFAULT_TIMEOUT = 10000L

fun ViewInteraction.waitUntilVisible(timeout: Long = DEFAULT_TIMEOUT): ViewInteraction {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + timeout
    do {
        try {
            check(matches(isDisplayed()))
            return this
        } catch (e: NoMatchingViewException) {
            Thread.sleep(TIMEOUT_INTERVAL)
        }
    } while (System.currentTimeMillis() < endTime)
    return this
}

fun setChecked(checked: Boolean) = object : ViewAction {
    val checkableViewMatcher = object : BaseMatcher<View>() {
        override fun matches(item: Any?): Boolean = isA(Checkable::class.java).matches(item)
        override fun describeTo(description: Description?) {
            description?.appendText("is Checkable instance ")
        }
    }

    override fun getConstraints(): BaseMatcher<View> = checkableViewMatcher
    override fun getDescription(): String? = null
    override fun perform(uiController: UiController?, view: View) {
        val checkableView: Checkable = view as Checkable
        if (checked != checkableView.isChecked) {
            click().perform(uiController, view)
        }
    }
}

/**
 * Helper function to clear shared preferences and delete database
 */
fun clearData() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val parentDirectory = context.filesDir.parentFile
    val sharedPreferencesFileNames = File(parentDirectory, "shared_prefs").list()
    sharedPreferencesFileNames?.forEach { fileName ->
        context.getSharedPreferences(fileName.replace(".xml", ""), Context.MODE_PRIVATE).edit()
            .clear().commit()
    }
    JudoRoomDatabase.getDatabase(InstrumentationRegistry.getInstrumentation().targetContext).clearAllTables()
    context.deleteDatabase("judo_database")
}

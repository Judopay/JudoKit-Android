package com.judokit.android.examples.test.robots

import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.DriverAtoms.webKeys
import androidx.test.espresso.web.webdriver.Locator

class ThreeDSecureRobot {
    fun enterTextIntoField(text: String, field: String) {
        onWebView().withElement(findElement(Locator.ID, field)).perform(webKeys(text))
    }

    fun tapOn(button: String) {
        onWebView().withElement(findElement(Locator.NAME, "UsernamePasswordEntry"))
            .perform(webClick())
    }
}

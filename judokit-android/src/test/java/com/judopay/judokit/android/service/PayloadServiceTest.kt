package com.judopay.judokit.android.service

import android.content.Context
import android.webkit.WebSettings
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Testing PayloadService")
class PayloadServiceTest {
    private val context: Context = mockk(relaxed = true)
    private val sut = PayloadService(context)
    private val gson = Gson()
    private var originalHttpAgent: String? = null

    @BeforeEach
    fun setUp() {
        mockkStatic(WebSettings::class)
        originalHttpAgent = System.getProperty("http.agent")
        System.clearProperty("http.agent")
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(WebSettings::class)
        if (originalHttpAgent != null) {
            System.setProperty("http.agent", originalHttpAgent!!)
        } else {
            System.clearProperty("http.agent")
        }
    }

    /*
     * getBrowserInfo is private; test via getEnhancedPaymentDetail and extract UserAgent from JSON.
     * Gson serializes private @SerializedName fields, so the traversal below is safe.
     */
    private fun resolvedUserAgent(): String =
        gson
            .toJsonTree(sut.getEnhancedPaymentDetail())
            .asJsonObject
            .getAsJsonObject("ConsumerDevice")
            .getAsJsonObject("ThreeDSecure")
            .getAsJsonObject("Browser")
            .get("UserAgent")
            .asString

    @Nested
    @DisplayName("getBrowserInfo")
    inner class GetBrowserInfo {
        @DisplayName("When WebSettings returns a user agent, then it is used as the browser user agent")
        @Test
        fun webSettingsUserAgentIsUsed() {
            every { WebSettings.getDefaultUserAgent(context) } returns "Mozilla/5.0 (Linux; Android 14)"

            assertEquals("Mozilla/5.0 (Linux; Android 14)", resolvedUserAgent())
        }

        @DisplayName("When WebSettings throws, then the http_agent system property is used as fallback")
        @Test
        fun systemPropertyIsUsedWhenWebSettingsThrows() {
            every { WebSettings.getDefaultUserAgent(context) } throws RuntimeException("WebView unavailable")
            System.setProperty("http.agent", "Dalvik/2.1.0")

            assertEquals("Dalvik/2.1.0", resolvedUserAgent())
        }

        @DisplayName("When WebSettings throws and http_agent property is null, then an empty string is returned")
        @Test
        fun emptyStringIsReturnedWhenBothSourcesUnavailable() {
            every { WebSettings.getDefaultUserAgent(context) } throws RuntimeException("WebView unavailable")
            // http.agent is cleared in setUp so getProperty returns null, which the ?: "" coalesces

            assertEquals("", resolvedUserAgent())
        }
    }
}

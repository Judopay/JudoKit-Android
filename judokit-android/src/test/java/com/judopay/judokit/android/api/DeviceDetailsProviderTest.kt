package com.judopay.judokit.android.api

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.UUID

internal class DeviceDetailsProviderTest {
    private val sharedPreferencesEditor = mockk<SharedPreferences.Editor>(relaxed = true)
    private val sharedPreferences =
        mockk<SharedPreferences> {
            every { edit() } returns sharedPreferencesEditor
        }
    private val context =
        mockk<Context> {
            every { applicationContext } returns this@mockk
            every { contentResolver } returns mockk()
            every { getSharedPreferences("DeviceDNA", Context.MODE_PRIVATE) } returns sharedPreferences
        }

    private val sut = DeviceDetailsProvider(context, androidVersion = "15")

    private lateinit var defaultLocale: Locale

    @BeforeEach
    internal fun setUp() {
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.UK)

        mockkStatic(Settings.Secure::class)
        every { Settings.Secure.getString(any(), Settings.Secure.ANDROID_ID) } returns "android-id"

        every { sharedPreferences.getString("Judo-vDeviceId", null) } returns "stored-v-device-id"
    }

    @AfterEach
    internal fun tearDown() {
        Locale.setDefault(defaultLocale)
        unmockkAll()
    }

    @DisplayName("Given device details are requested, then kDeviceId is the Android ID")
    @Test
    fun kDeviceIdIsAndroidId() {
        assertEquals("android-id", sut.deviceDetails.kDeviceId)
    }

    @DisplayName("Given a vDeviceId is already stored, then return the stored value")
    @Test
    fun vDeviceIdReturnsStoredValue() {
        assertEquals("stored-v-device-id", sut.deviceDetails.vDeviceId)
    }

    @DisplayName("Given no vDeviceId is stored, then generate and persist a new UUID")
    @Test
    fun vDeviceIdGeneratedAndPersistedWhenMissing() {
        every { sharedPreferences.getString("Judo-vDeviceId", null) } returns null

        val vDeviceId = sut.deviceDetails.vDeviceId

        assertNotNull(UUID.fromString(vDeviceId))
        verify { sharedPreferencesEditor.putString("Judo-vDeviceId", vDeviceId) }
    }

    @DisplayName("Given the default locale is UK, then countryCode is the ISO alpha-2 code")
    @Test
    fun countryCodeIsIsoAlpha2Code() {
        assertEquals("GB", sut.deviceDetails.countryCode)
    }

    @DisplayName("Given the default locale is UK, then cultureLocale is language_country")
    @Test
    fun cultureLocaleIsLanguageAndCountry() {
        assertEquals("en_GB", sut.deviceDetails.cultureLocale)
    }

    @DisplayName("Given device details are requested, then os contains the Android version")
    @Test
    fun osContainsAndroidVersion() {
        assertEquals("Android 15", sut.deviceDetails.os)
    }

    @DisplayName("Given device details are requested twice, then vDeviceId is only read once")
    @Test
    fun stableValuesAreCached() {
        sut.deviceDetails
        sut.deviceDetails

        verify(exactly = 1) { sharedPreferences.getString("Judo-vDeviceId", null) }
        verify(exactly = 1) { Settings.Secure.getString(any(), Settings.Secure.ANDROID_ID) }
    }
}

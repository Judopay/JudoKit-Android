package com.judopay.judokit.android.ui.common

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.view.View
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@DisplayName("Testing the helper functions logic")
internal class FunctionsKtTest {
    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    @DisplayName("isValidLuhnNumber")
    inner class IsValidLuhnNumberTests {
        @Test
        @DisplayName("When a valid number is specified, isValidLuhnNumber() should return true")
        fun testThatWhenAValidLuhnNumberIsProvidedLuhnCheckReturnsTrue() {
            assertTrue(isValidLuhnNumber("1234567812345670"))
        }

        @Test
        @DisplayName("When a non-numeric input is specified, isValidLuhnNumber() should return false")
        fun testThatWhenANonNumericStringIsProvidedLuhnCheckReturnsFalse() {
            assertFalse(isValidLuhnNumber("asad"))
        }

        @Test
        @DisplayName("When a number that fails Luhn check is specified, isValidLuhnNumber() should return false")
        fun testThatWhenAnInvalidLuhnNumberIsProvidedLuhnCheckReturnsFalse() {
            assertFalse(isValidLuhnNumber("4111111111111112"))
        }

        @Test
        @DisplayName("Empty string passes Luhn check (sum is zero)")
        fun emptyStringPassesLuhnCheck() {
            assertTrue(isValidLuhnNumber(""))
        }

        @Test
        @DisplayName("Single zero digit passes Luhn check")
        fun singleZeroDigitPassesLuhnCheck() {
            assertTrue(isValidLuhnNumber("0"))
        }

        @Test
        @DisplayName("String with spaces fails Luhn check")
        fun stringWithSpacesFailsLuhnCheck() {
            assertFalse(isValidLuhnNumber("4111 1111 1111 1111"))
        }
    }

    @Nested
    @DisplayName("isDependencyPresent")
    inner class IsDependencyPresentTests {
        @Test
        @DisplayName("When isDependencyPresent is called with a class that exists, it should return true")
        fun testIsDependencyPresentReturnsTrueForExistingClass() {
            assertTrue(isDependencyPresent("java.lang.String"))
        }

        @Test
        @DisplayName("When isDependencyPresent is called with a class that does not exist, it should return false")
        fun testIsDependencyPresentReturnsFalseForMissingClass() {
            assertFalse(isDependencyPresent("com.nonexistent.SomeClass"))
        }
    }

    @Nested
    @DisplayName("toDate")
    inner class ToDateTests {
        @Test
        @DisplayName("Valid ISO timestamp is parsed correctly")
        fun validTimestampIsParsedCorrectly() {
            val timestamp = "2023-01-15T10:30:00.000Z"
            val expected = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(timestamp)!!
            val actual = toDate(timestamp, Locale.US)
            assertEquals(expected.time, actual.time)
        }

        @Test
        @DisplayName("Invalid timestamp returns current time as fallback")
        fun invalidTimestampReturnsFallbackDate() {
            val before = Date().time
            val result = toDate("not-a-valid-date", Locale.US)
            assertTrue(result.time >= before)
        }

        @Test
        @DisplayName("Custom pattern is used when provided")
        fun customPatternIsUsed() {
            val expected = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse("25/12/2022")!!
            val actual = toDate("25/12/2022", Locale.US, "dd/MM/yyyy")
            assertEquals(expected.time, actual.time)
        }
    }

    @Nested
    @DisplayName("getLocale")
    inner class GetLocaleTests {
        @Test
        @DisplayName("Returns primary locale from resource configuration")
        fun returnsPrimaryLocale() {
            mockkStatic(ConfigurationCompat::class)
            val resources = mockk<Resources>()
            val config = mockk<Configuration>()
            val localeList = mockk<LocaleListCompat>()
            every { resources.configuration } returns config
            every { ConfigurationCompat.getLocales(config) } returns localeList
            every { localeList[0] } returns Locale.FRENCH

            assertEquals(Locale.FRENCH, getLocale(resources))
        }

        @Test
        @DisplayName("Falls back to Locale.getDefault() when locale list returns null")
        fun fallsBackToDefaultLocaleWhenListReturnsNull() {
            mockkStatic(ConfigurationCompat::class)
            val resources = mockk<Resources>()
            val config = mockk<Configuration>()
            val localeList = mockk<LocaleListCompat>()
            every { resources.configuration } returns config
            every { ConfigurationCompat.getLocales(config) } returns localeList
            every { localeList[0] } returns null

            assertEquals(Locale.getDefault(), getLocale(resources))
        }
    }

    @Nested
    @DisplayName("isInternetAvailable")
    inner class IsInternetAvailableTests {
        @Test
        @DisplayName("Returns false when ConnectivityManager is not available")
        fun returnsFalseWhenConnectivityManagerIsNull() {
            val context = mockk<Context>()
            every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns null
            assertFalse(isInternetAvailable(context))
        }

        @Test
        @DisplayName("Returns false when activeNetwork is null")
        fun returnsFalseWhenActiveNetworkIsNull() {
            val context = mockk<Context>()
            val cm = mockk<ConnectivityManager>()
            every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns cm
            every { cm.activeNetwork } returns null
            assertFalse(isInternetAvailable(context))
        }

        @Test
        @DisplayName("Returns false when NetworkCapabilities is null")
        fun returnsFalseWhenNetworkCapabilitiesIsNull() {
            val context = mockk<Context>()
            val cm = mockk<ConnectivityManager>()
            val network = mockk<Network>()
            every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns cm
            every { cm.activeNetwork } returns network
            every { cm.getNetworkCapabilities(network) } returns null
            assertFalse(isInternetAvailable(context))
        }

        @Test
        @DisplayName("Returns true for WiFi transport")
        fun returnsTrueForWifiNetwork() {
            val context = mockk<Context>()
            val cm = mockk<ConnectivityManager>()
            val network = mockk<Network>()
            val capabilities = mockk<NetworkCapabilities>()
            every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns cm
            every { cm.activeNetwork } returns network
            every { cm.getNetworkCapabilities(network) } returns capabilities
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
            assertTrue(isInternetAvailable(context))
        }

        @Test
        @DisplayName("Returns true for cellular transport")
        fun returnsTrueForMobileNetwork() {
            val context = mockk<Context>()
            val cm = mockk<ConnectivityManager>()
            val network = mockk<Network>()
            val capabilities = mockk<NetworkCapabilities>()
            every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns cm
            every { cm.activeNetwork } returns network
            every { cm.getNetworkCapabilities(network) } returns capabilities
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
            assertTrue(isInternetAvailable(context))
        }

        @Test
        @DisplayName("Returns true for ethernet transport")
        fun returnsTrueForEthernetNetwork() {
            val context = mockk<Context>()
            val cm = mockk<ConnectivityManager>()
            val network = mockk<Network>()
            val capabilities = mockk<NetworkCapabilities>()
            every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns cm
            every { cm.activeNetwork } returns network
            every { cm.getNetworkCapabilities(network) } returns capabilities
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns true
            assertTrue(isInternetAvailable(context))
        }

        @Test
        @DisplayName("Returns false for an unrecognised transport type")
        fun returnsFalseForUnknownNetworkType() {
            val context = mockk<Context>()
            val cm = mockk<ConnectivityManager>()
            val network = mockk<Network>()
            val capabilities = mockk<NetworkCapabilities>()
            every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns cm
            every { cm.activeNetwork } returns network
            every { cm.getNetworkCapabilities(network) } returns capabilities
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
            every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false
            assertFalse(isInternetAvailable(context))
        }
    }

    @Nested
    @DisplayName("heightWithInsetsAndMargins")
    inner class HeightWithInsetsAndMarginsTests {
        @Test
        @DisplayName("Sums height, padding and margins when MarginLayoutParams is present")
        fun sumsHeightPaddingAndMargins() {
            val view = mockk<View>()
            val params = mockk<ViewGroup.MarginLayoutParams>()
            params.topMargin = 8
            params.bottomMargin = 12
            every { view.height } returns 100
            every { view.paddingTop } returns 10
            every { view.paddingBottom } returns 5
            every { view.layoutParams } returns params

            assertEquals(135, view.heightWithInsetsAndMargins)
        }

        @Test
        @DisplayName("Returns height plus padding only when layoutParams is not MarginLayoutParams")
        fun returnsHeightAndPaddingOnlyWithoutMarginParams() {
            val view = mockk<View>()
            every { view.height } returns 50
            every { view.paddingTop } returns 4
            every { view.paddingBottom } returns 6
            every { view.layoutParams } returns mockk<ViewGroup.LayoutParams>()

            assertEquals(60, view.heightWithInsetsAndMargins)
        }

        @Test
        @DisplayName("Returns height plus padding when layoutParams is null")
        fun returnsHeightAndPaddingWhenLayoutParamsIsNull() {
            val view = mockk<View>()
            every { view.height } returns 30
            every { view.paddingTop } returns 2
            every { view.paddingBottom } returns 3
            every { view.layoutParams } returns null

            assertEquals(35, view.heightWithInsetsAndMargins)
        }
    }

    @Nested
    @DisplayName("viewModelFactory")
    inner class ViewModelFactoryTests {
        @Test
        @DisplayName("Factory creates the ViewModel instance supplied by the creator lambda")
        fun factoryCreatesCorrectViewModelInstance() {
            class TestViewModel : ViewModel()
            val factory = viewModelFactory { TestViewModel() }
            val created = factory.create(TestViewModel::class.java)
            assertTrue(created is TestViewModel)
        }
    }
}

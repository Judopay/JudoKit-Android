package com.judopay.judokit.android.api.interceptor

import android.content.Context
import com.judopay.judokit.android.api.exception.NetworkConnectivityException
import com.judopay.judokit.android.ui.common.isInternetAvailable
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import okhttp3.Interceptor
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing NetworkConnectivityInterceptor")
internal class NetworkConnectivityInterceptorTest {
    private val context = mockk<Context>()

    private val sut = NetworkConnectivityInterceptor(context)

    @BeforeEach
    internal fun setUp() {
        mockkStatic("com.judopay.judokit.android.ui.common.FunctionsKt")
    }

    @DisplayName("Given internet is available, then don't throw exception")
    @Test
    fun doesNotThrowExceptionWhenInternetAvailable() {
        every { isInternetAvailable(context) } returns true
        val chain: Interceptor.Chain = mockk(relaxed = true)

        assertDoesNotThrow { sut.intercept(chain) }
    }

    @DisplayName("Given internet is unavailable, then throw NetworkConnectivityException")
    @Test
    fun throwsExceptionWhenTimeout() {
        every { isInternetAvailable(context) } returns false
        val chain: Interceptor.Chain = mockk(relaxed = true)

        assertThrows<NetworkConnectivityException> { sut.intercept(chain) }
    }
}

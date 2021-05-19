package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing NetworkTimeout builder class")
internal class NetworkTimeoutTest {
    private val sut = NetworkTimeout.Builder()

    @DisplayName("Given connectTimeout is null, then default connectTimeout should be set")
    @Test
    fun setDefaultConnectTimeoutWhenNull() {
        assertEquals(5L, sut.build().connectTimeout)
    }

    @DisplayName("Given readTimeout is null, then default readTimeout should be set")
    @Test
    fun setDefaultReadTimeoutWhenNull() {
        assertEquals(180L, sut.build().readTimeout)
    }

    @DisplayName("Given writeTimeout is null, then default writeTimeout should be set")
    @Test
    fun setDefaultWriteTimeoutWhenNull() {
        assertEquals(30L, sut.build().writeTimeout)
    }

    @DisplayName("Given connectTimeout is set, then connectTimeout should return the set value")
    @Test
    fun returnConnectTimeoutWhenSet() {
        assertEquals(10L, sut.setConnectTimeout(10L).build().connectTimeout)
    }

    @DisplayName("Given readTimeout is set, then readTimeout should return the set value")
    @Test
    fun returnReadTimeoutWhenSet() {
        assertEquals(200L, sut.setReadTimeout(200L).build().readTimeout)
    }

    @DisplayName("Given writeTimeout is set, then writeTimeout should return the set value")
    @Test
    fun returnWriteTimeoutWhenSet() {
        assertEquals(80L, sut.setWriteTimeout(80L).build().writeTimeout)
    }

    @DisplayName("Given connectTimeout has been set to a value not in allowed range, then throw IllegalArgumentException")
    @Test
    fun throwExceptionWhenConnectTimeoutSetToWrongValue() {
        assertThrows<IllegalArgumentException> { sut.setConnectTimeout(4L).build() }
        assertThrows<IllegalArgumentException> { sut.setConnectTimeout(31L).build() }
    }

    @DisplayName("Given readTimeout has been set to a value not in allowed range, then throw IllegalArgumentException")
    @Test
    fun throwExceptionWhenReadTimeoutSetToWrongValue() {
        assertThrows<IllegalArgumentException> { sut.setReadTimeout(179L).build() }
        assertThrows<IllegalArgumentException> { sut.setReadTimeout(301L).build() }
    }

    @DisplayName("Given connectTimeout has been set to a value not in allowed range, then throw IllegalArgumentException")
    @Test
    fun throwExceptionWhenWriteTimeoutSetToWrongValue() {
        assertThrows<IllegalArgumentException> { sut.setWriteTimeout(29L).build() }
        assertThrows<IllegalArgumentException> { sut.setWriteTimeout(121L).build() }
    }
}

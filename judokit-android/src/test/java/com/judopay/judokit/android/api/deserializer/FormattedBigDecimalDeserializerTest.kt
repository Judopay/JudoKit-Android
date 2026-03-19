package com.judopay.judokit.android.api.deserializer

import com.google.gson.JsonPrimitive
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Testing FormattedBigDecimalDeserializer")
internal class FormattedBigDecimalDeserializerTest {
    private val deserializer = FormattedBigDecimalDeserializer()

    @Test
    @DisplayName("Given a plain decimal string, then it deserializes correctly")
    fun deserializePlainDecimal() {
        val json = JsonPrimitive("1234.56")
        val result = deserializer.deserialize(json, BigDecimal::class.java, mockk())
        assertEquals(BigDecimal("1234.56"), result)
    }

    @Test
    @DisplayName("Given a decimal string with commas, then commas are stripped")
    fun deserializeDecimalWithCommas() {
        val json = JsonPrimitive("1,234.56")
        val result = deserializer.deserialize(json, BigDecimal::class.java, mockk())
        assertEquals(BigDecimal("1234.56"), result)
    }

    @Test
    @DisplayName("Given an empty string, then it returns BigDecimal zero")
    fun deserializeEmptyString() {
        val json = JsonPrimitive("")
        val result = deserializer.deserialize(json, BigDecimal::class.java, mockk())
        assertEquals(BigDecimal(0), result)
    }

    @Test
    @DisplayName("Given a whole number string, then it deserializes correctly")
    fun deserializeWholeNumber() {
        val json = JsonPrimitive("100")
        val result = deserializer.deserialize(json, BigDecimal::class.java, mockk())
        assertEquals(BigDecimal("100"), result)
    }

    @Test
    @DisplayName("Given a string with multiple commas, then all commas are stripped")
    fun deserializeWithMultipleCommas() {
        val json = JsonPrimitive("1,000,000.00")
        val result = deserializer.deserialize(json, BigDecimal::class.java, mockk())
        assertEquals(BigDecimal("1000000.00"), result)
    }
}

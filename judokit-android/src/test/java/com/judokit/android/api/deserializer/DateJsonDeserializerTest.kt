package com.judokit.android.api.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.lang.reflect.Type

@DisplayName("Testing DateJsonDeserializer")
internal class DateJsonDeserializerTest {

    private val jsonElement: JsonElement = mockk(relaxed = true)
    private val type: Type = mockk(relaxed = true)
    private val jsonDeserializationContext: JsonDeserializationContext = mockk(relaxed = true)

    private val sut = DateJsonDeserializer()

    @DisplayName("Given date is specified, then deserialize date")
    @Test
    fun shouldDeserializeDate() {
        every { jsonElement.asString } returns "2016-03-24T09:55:28.3299+00:00"

        val actual = sut.deserialize(jsonElement, type, jsonDeserializationContext)?.time
        val expected = 1458813328329L

        assertEquals(expected, actual)
    }

    @DisplayName("Given date is specified, then deserialize date with 3 digit millisecond precision")
    @Test
    fun shouldDeserializeDateWith3DigitMillisecondPrecision() {
        every { jsonElement.asString } returns "2016-03-24T09:55:28.329+00:00"

        val actual = sut.deserialize(jsonElement, type, jsonDeserializationContext)?.time
        val expected = 1458813328329L

        assertEquals(expected, actual)
    }

    @DisplayName("Given date is specified, then deserialize date with positive timezone offset")
    @Test
    fun shouldDeserializeDateWithPositiveTimezoneOffset() {
        every { jsonElement.asString } returns "2016-03-24T09:55:28.3299+01:00"

        val actual = sut.deserialize(jsonElement, type, jsonDeserializationContext)?.time
        val expected = 1458809728329L

        assertEquals(expected, actual)
    }

    @DisplayName("Given date is specified, then deserialize date with negative timezone offset")
    @Test
    fun shouldDeserializeDateWithNegativeTimezoneOffset() {
        every { jsonElement.asString } returns "2016-03-24T09:55:28.3299-02:00"

        val actual = sut.deserialize(jsonElement, type, jsonDeserializationContext)?.time
        val expected = 1458820528329L

        assertEquals(expected, actual)
    }
}

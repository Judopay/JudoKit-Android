package com.judopay.api;

import com.google.gson.JsonElement;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormattedBigDecimalDeserializerTest {

    @Test
    public void shouldReturnZeroWhenNull() {
        FormattedBigDecimalDeserializer deserializer = new FormattedBigDecimalDeserializer();
        JsonElement jsonElement = mock(JsonElement.class);

        BigDecimal result = deserializer.deserialize(jsonElement, null, null);
        BigDecimal zero = new BigDecimal(0);
        assertThat(result, equalTo(zero));
    }

    @Test
    public void shouldReturnNumberWhenNoThousandSeparator() {
        FormattedBigDecimalDeserializer deserializer = new FormattedBigDecimalDeserializer();
        JsonElement jsonElement = mock(JsonElement.class);

        when(jsonElement.getAsString()).thenReturn("123.45");

        BigDecimal result = deserializer.deserialize(jsonElement, null, null);
        BigDecimal expected = new BigDecimal("123.45");
        assertThat(result, equalTo(expected));
    }

    @Test
    public void shouldReturnNumberWhenThousandSeparator() {
        FormattedBigDecimalDeserializer deserializer = new FormattedBigDecimalDeserializer();
        JsonElement jsonElement = mock(JsonElement.class);

        when(jsonElement.getAsString()).thenReturn("1,234.56");

        BigDecimal result = deserializer.deserialize(jsonElement, null, null);
        BigDecimal expected = new BigDecimal("1234.56");
        assertThat(result, equalTo(expected));
    }

}
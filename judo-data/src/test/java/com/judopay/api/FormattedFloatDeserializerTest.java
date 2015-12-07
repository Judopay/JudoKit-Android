package com.judopay.api;

import com.google.gson.JsonElement;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormattedFloatDeserializerTest {

    @Test
    public void shouldReturnZeroWhenNull() {
        FormattedFloatDeserializer deserializer = new FormattedFloatDeserializer();
        JsonElement jsonElement = mock(JsonElement.class);

        Float result = deserializer.deserialize(jsonElement, null, null);
        Float zero = 0f;
        assertThat(result, equalTo(zero));
    }

    @Test
    public void shouldReturnNumberWhenNoThousandSeparator() {
        FormattedFloatDeserializer deserializer = new FormattedFloatDeserializer();
        JsonElement jsonElement = mock(JsonElement.class);

        when(jsonElement.getAsString()).thenReturn("123.45");

        Float result = deserializer.deserialize(jsonElement, null, null);
        Float expected = 123.45f;
        assertThat(result, equalTo(expected));
    }

    @Test
    public void shouldReturnNumberWhenThousandSeparator() {
        FormattedFloatDeserializer deserializer = new FormattedFloatDeserializer();
        JsonElement jsonElement = mock(JsonElement.class);

        when(jsonElement.getAsString()).thenReturn("1,234.56");

        Float result = deserializer.deserialize(jsonElement, null, null);
        Float expected = 1234.56f;
        assertThat(result, equalTo(expected));
    }

}
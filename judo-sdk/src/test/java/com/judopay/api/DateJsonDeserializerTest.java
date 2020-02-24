package com.judopay.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.judopay.api.deserializer.DateJsonDeserializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Type;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DateJsonDeserializerTest {

    @Mock
    private JsonElement jsonElement;

    @Mock
    private Type type;

    @Mock
    private JsonDeserializationContext jsonDeserializationContext;

    @Test
    public void shouldDeserializeDate() {
        DateJsonDeserializer deserializer = new DateJsonDeserializer();

        when(jsonElement.getAsString()).thenReturn("2016-03-24T09:55:28.3299+00:00");
        Date date = deserializer.deserialize(jsonElement, type, jsonDeserializationContext);

        assertThat(date.getTime(), is(1458813328329L));
    }

    @Test
    public void shouldDeserializeDateWith3DigitMillisecondPrecision() {
        DateJsonDeserializer deserializer = new DateJsonDeserializer();

        when(jsonElement.getAsString()).thenReturn("2016-03-24T09:55:28.329+00:00");
        Date date = deserializer.deserialize(jsonElement, type, jsonDeserializationContext);

        assertThat(date.getTime(), is(1458813328329L));
    }

    @Test
    public void shouldDeserializeDateWithPositiveTimezoneOffset() {
        DateJsonDeserializer deserializer = new DateJsonDeserializer();

        when(jsonElement.getAsString()).thenReturn("2016-03-24T09:55:28.3299+01:00");
        Date date = deserializer.deserialize(jsonElement, type, jsonDeserializationContext);

        assertThat(date.getTime(), is(1458809728329L));
    }

    @Test
    public void shouldDeserializeDateWithNegativeTimezoneOffset() {
        DateJsonDeserializer deserializer = new DateJsonDeserializer();

        when(jsonElement.getAsString()).thenReturn("2016-03-24T09:55:28.3299-02:00");
        Date date = deserializer.deserialize(jsonElement, type, jsonDeserializationContext);

        assertThat(date.getTime(), is(1458820528329L));
    }
}

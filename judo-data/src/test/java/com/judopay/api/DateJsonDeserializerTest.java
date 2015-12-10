package com.judopay.api;

import com.google.gson.JsonElement;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DateJsonDeserializerTest {

    @Test
    public void shouldDeserializeDate() {
        DateJsonDeserializer dateJsonDeserializer = new DateJsonDeserializer();
        JsonElement jsonElement = mock(JsonElement.class);
        when(jsonElement.getAsString())
                .thenReturn("2015-09-28T15:39:24.0193+01:00");

        Date date = dateJsonDeserializer.deserialize(jsonElement, null, null);
        assertThat(date.getTime(), equalTo(1443451164019L));
    }

}
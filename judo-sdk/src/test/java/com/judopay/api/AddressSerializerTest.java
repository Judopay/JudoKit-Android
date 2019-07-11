package com.judopay.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.judopay.model.Address;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class AddressSerializerTest {

    @Mock
    private Type type;

    @Mock
    private JsonSerializationContext jsonSerializationContext;

    @Test
    public void shouldSerializeAllFields() {
        Address.Serializer serializer = new Address.Serializer();
        Address address = new Address("line 1", "line 2", "line 3", "town", "postCode", 826);

        JsonElement json = serializer.serialize(address, type, jsonSerializationContext);
        JsonObject jsonObject = json.getAsJsonObject();

        assertThat(jsonObject.get("line1").getAsString(), equalTo("line 1"));
        assertThat(jsonObject.get("line2").getAsString(), equalTo("line 2"));
        assertThat(jsonObject.get("line2").getAsString(), equalTo("line 2"));
        assertThat(jsonObject.get("town").getAsString(), equalTo("town"));
        assertThat(jsonObject.get("postCode").getAsString(), equalTo("postCode"));

        assertThat(jsonObject.get("countryCode").getAsInt(), equalTo(826));
    }

    @Test
    public void shouldNotSerializeCountryCodeIfZero() {
        Address.Serializer serializer = new Address.Serializer();
        Address address = new Address("line 1", "line 2", "line 3", "town", "postCode", 0);

        JsonElement json = serializer.serialize(address, type, jsonSerializationContext);
        JsonObject jsonObject = json.getAsJsonObject();

        assertThat(jsonObject.get("countryCode"), equalTo(null));
    }
}

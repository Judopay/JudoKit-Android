package com.judopay.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

class FormattedFloatDeserializer implements JsonDeserializer<Float> {

    @Override
    public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String jsonString = json.getAsString();
        if (jsonString != null && jsonString.length() > 0) {
            return Float.parseFloat(jsonString.replaceAll(",", ""));
        } else {
            return 0f;
        }
    }

}
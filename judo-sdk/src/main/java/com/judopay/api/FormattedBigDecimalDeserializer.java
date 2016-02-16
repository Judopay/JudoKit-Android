package com.judopay.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import static com.judopay.arch.TextUtil.isEmpty;

class FormattedBigDecimalDeserializer implements JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String jsonString = json.getAsString();
        if (!isEmpty(jsonString)) {
            return new BigDecimal(jsonString.replaceAll(",", ""));
        } else {
            return new BigDecimal(0);
        }
    }

}
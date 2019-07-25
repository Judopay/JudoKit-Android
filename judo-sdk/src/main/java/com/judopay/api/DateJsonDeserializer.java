package com.judopay.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import static com.judopay.arch.TextUtil.isEmpty;

class DateJsonDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        String jsonAsString = json.getAsString();
        Date date = null;
        if (!isEmpty(jsonAsString)) {
            date = Iso8601Deserializer.toDate(jsonAsString);
        }
        return date;
    }
}
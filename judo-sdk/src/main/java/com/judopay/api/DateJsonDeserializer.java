package com.judopay.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import fr.turri.jiso8601.Iso8601Deserializer;

import static com.judopay.arch.TextUtil.isEmpty;

class DateJsonDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String date = json.getAsString();

        if (!isEmpty(date)) {
            return Iso8601Deserializer.toDate(date);
        } else {
            return null;
        }
    }

}
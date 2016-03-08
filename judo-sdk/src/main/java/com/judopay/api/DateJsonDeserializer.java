package com.judopay.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.Date;

import static com.judopay.arch.TextUtil.isEmpty;

class DateJsonDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String date = json.getAsString();

        if (!isEmpty(date)) {
            DateTime dateTime = new DateTime(date);
            return dateTime.toDate();
        } else {
            return null;
        }
    }

}

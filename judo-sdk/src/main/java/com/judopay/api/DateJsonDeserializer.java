package com.judopay.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.judopay.arch.TextUtil.isEmpty;

class DateJsonDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String date = json.getAsString();

        if (!isEmpty(date)) {
            try {
                return iso8601ToDate(date);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Transforms a ISO 8601 string to a Date
     *
     * @param date the date as defined in ISO-8601 format
     * @return the parsed date
     * @throws ParseException
     */
    public Date iso8601ToDate(final String date) throws ParseException {
        String s = date.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US).parse(s);
    }

}
package com.judokit.android.api.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.util.Date

internal class DateJsonDeserializer : JsonDeserializer<Date?> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Date? {

        val string = json.asString

        if (string.isNotEmpty()) {
            return Iso8601Deserializer.toDate(string)
        }

        return null
    }
}

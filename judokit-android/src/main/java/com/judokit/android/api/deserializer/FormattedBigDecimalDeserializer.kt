package com.judokit.android.api.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.math.BigDecimal

internal class FormattedBigDecimalDeserializer : JsonDeserializer<BigDecimal> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BigDecimal {
        val jsonString = json.asString
        return if (jsonString.isNotEmpty()) {
            BigDecimal(jsonString.replace(",".toRegex(), ""))
        } else {
            BigDecimal(0)
        }
    }
}

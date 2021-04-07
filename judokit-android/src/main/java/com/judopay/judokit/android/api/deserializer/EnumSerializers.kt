package com.judopay.judokit.android.api.deserializer

import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption
import java.io.NotSerializableException
import java.lang.reflect.Type

internal class ScaExemptionSerializer : JsonSerializer<ScaExemption> {

    @Throws(JsonParseException::class)
    override fun serialize(
        src: ScaExemption,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return context?.serialize(src.value) ?: throw NotSerializableException(src::class.java.name)
    }
}
internal class ChallengeRequestIndicatorSerializer : JsonSerializer<ChallengeRequestIndicator> {

    @Throws(JsonParseException::class)
    override fun serialize(
        src: ChallengeRequestIndicator,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return context?.serialize(src.value) ?: throw NotSerializableException(src::class.java.name)
    }
}

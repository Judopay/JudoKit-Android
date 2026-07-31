package com.judokit.android.examples.feature

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.judopay.judokit.android.model.googlepay.GooglePayDisplayItem

internal object GooglePayDisplayItemsParser {
    private val gson = Gson()
    private val listType = object : TypeToken<List<GooglePayDisplayItem>>() {}.type

    fun parse(json: String?): List<GooglePayDisplayItem>? {
        val trimmed = json?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            return null
        }

        return try {
            gson.fromJson<List<GooglePayDisplayItem>>(trimmed, listType)
                .takeIf { it.isNotEmpty() }
        } catch (_: JsonSyntaxException) {
            null
        }
    }
}

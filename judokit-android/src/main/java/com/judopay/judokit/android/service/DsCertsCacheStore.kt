package com.judopay.judokit.android.service

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.api.model.response.cdn.DsCertsCache

private const val PREFS_NAME = "judokit_ds_certs"
private const val KEY_CACHE = "cache_v1"

internal class DsCertsCacheStore(
    context: Context,
    private val gson: Gson = GsonBuilder().create(),
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun read(): DsCertsCache? {
        val json = prefs.getString(KEY_CACHE, null) ?: return null
        return runCatching { gson.fromJson(json, DsCertsCache::class.java) }.getOrNull()
    }

    fun write(cache: DsCertsCache) {
        prefs.edit { putString(KEY_CACHE, gson.toJson(cache)) }
    }
}

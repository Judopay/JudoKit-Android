package com.judopay.judokit.android.api.interceptor

import android.content.Context
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.judopay.devicedna.DeviceDNA
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException

private const val CLIENT_DETAILS = "clientDetails"
private const val METHOD_POST = "POST"
private val MEDIA_TYPE_APPLICATION_JSON = "application/json".toMediaTypeOrNull()

internal class DeviceDnaInterceptor(context: Context) : Interceptor {
    private val deviceDna = DeviceDNA(context)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body

        if (request.isPost && body != null) {
            val bodyJson = getJsonRequestBody(body)
            if (bodyJson.isJsonObject) {
                val json = bodyJson.asJsonObject

                if (json.get(CLIENT_DETAILS) == null) {
                    addClientDetails(json)
                }

                val postJson = json.toString().toRequestBody(MEDIA_TYPE_APPLICATION_JSON)
                return chain.proceed(
                    request.newBuilder()
                        .post(postJson)
                        .build(),
                )
            }
        }
        return chain.proceed(request)
    }

    private fun addClientDetails(json: JsonObject) {
        val signals = deviceDna.dna
        val clientDetailsJson = JsonObject()
        for ((key, value) in signals) {
            clientDetailsJson.addProperty(key, value)
        }
        json.add(CLIENT_DETAILS, clientDetailsJson)
    }

    @Throws(IOException::class)
    private fun getJsonRequestBody(request: RequestBody): JsonElement {
        val buffer = Buffer()
        request.writeTo(buffer)
        val body = buffer.readUtf8()
        return JsonParser.parseString(body)
    }
}

private val Request.isPost: Boolean
    get() = METHOD_POST == method

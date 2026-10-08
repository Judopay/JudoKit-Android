package com.judopay.judokit.android.api.interceptor

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.judopay.judokit.android.api.DeviceDetailsProvider
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException

private const val DEVICE_DETAILS = "deviceDetails"

internal class DeviceDnaInterceptor(
    context: Context,
    private val deviceDetailsProvider: DeviceDetailsProvider = DeviceDetailsProvider(context),
) : Interceptor {
    private val gson = Gson()

    @Throws(IOException::class)
    @Suppress("ReturnCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body

        if (request.method != "POST" || body == null || !body.isRewritableJson) {
            return chain.proceed(request)
        }

        val postJson = body.bodyAsJsonObject()

        if (postJson == null || postJson.has(DEVICE_DETAILS)) {
            return chain.proceed(request)
        }

        postJson.add(DEVICE_DETAILS, gson.toJsonTree(deviceDetailsProvider.deviceDetails))

        val requestBody = postJson.toString().toRequestBody(body.contentType())

        return chain.proceed(
            request
                .newBuilder()
                .post(requestBody)
                .build(),
        )
    }

    private fun RequestBody.bodyAsJsonObject(): JsonObject? =
        try {
            val buffer = Buffer().also { writeTo(it) }
            JsonParser.parseString(buffer.readUtf8()).asJsonObject
        } catch (ignore: IOException) {
            null
        } catch (ignore: JsonParseException) {
            null
        } catch (ignore: IllegalStateException) {
            null
        }
}

// A one-shot body cannot be buffered here and then written again when the request is sent.
private val RequestBody.isRewritableJson: Boolean
    get() = !isOneShot() && contentType()?.subtype == "json"

package com.judopay.judokit.android.api.interceptor

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
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
    @Suppress("SwallowedException", "TooGenericExceptionCaught", "ReturnCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body

        if (!request.isPost || body == null) {
            return chain.proceed(request)
        }

        val postJson = body.bodyAsJsonObject()

        if (postJson == null || postJson.get(CLIENT_DETAILS) != null) {
            return chain.proceed(request)
        }

        addClientDetails(postJson)

        val requestBody = postJson.toString().toRequestBody(MEDIA_TYPE_APPLICATION_JSON)

        return chain.proceed(
            request.newBuilder()
                .post(requestBody)
                .build(),
        )
    }

    private fun addClientDetails(json: JsonObject) {
        val signals = deviceDna.dna
        val clientDetailsJson = JsonObject()
        for ((key, value) in signals) {
            clientDetailsJson.addProperty(key, value)
        }
        json.add(CLIENT_DETAILS, clientDetailsJson)
    }

    private fun RequestBody.bodyAsJsonObject(): JsonObject? {
        try {
            val buffer = Buffer()
            writeTo(buffer)
            val body = buffer.readUtf8()
            val jsonElement = JsonParser.parseString(body)
            return jsonElement.asJsonObject
        } catch (ignore: IOException) {
        } catch (ignore: JsonParseException) {
        } catch (ignore: JsonSyntaxException) {
        } catch (ignore: IllegalStateException) {
        }
        return null
    }
}

private val Request.isPost: Boolean
    get() = METHOD_POST == method

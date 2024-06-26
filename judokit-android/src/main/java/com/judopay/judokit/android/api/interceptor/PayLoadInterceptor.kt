package com.judopay.judokit.android.api.interceptor

import android.content.Context
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.judopay.judokit.android.service.PayloadService
import com.judopay.judokit.android.toJSONString
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException

private const val ENHANCED_PAYMENT_DETAIL = "EnhancedPaymentDetail"
private val PAYLOAD_ENDPOINTS =
    arrayListOf(
        "/transactions/payments",
        "/transactions/preauths",
        "/transactions/registercard",
        "/transactions/checkcard",
    )

class PayLoadInterceptor internal constructor(context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (PAYLOAD_ENDPOINTS.contains(path) && request.body != null) {
            convertRequestBodyToJson(request)?.let {
                val json = it.asJsonObject
                if (json[ENHANCED_PAYMENT_DETAIL] == null) {
                    json.add(ENHANCED_PAYMENT_DETAIL, enhancedPaymentDetail)
                }

                return chain.proceed(
                    request.newBuilder()
                        .post(convertJsonToRequestBody(json))
                        .build(),
                )
            }
        }

        return chain.proceed(request)
    }

    private val payloadService = PayloadService(context)

    @Suppress("SwallowedException")
    private val enhancedPaymentDetail: JsonObject
        get() {
            val paymentDetail = payloadService.getEnhancedPaymentDetail()?.toJSONString()
            return try {
                val jsonElement = JsonParser().parse(paymentDetail)
                jsonElement.asJsonObject
            } catch (e: JsonSyntaxException) {
                JsonObject()
            }
        }

    private fun convertJsonToRequestBody(json: JsonObject): RequestBody {
        val mediaType = "application/json".toMediaTypeOrNull()
        return RequestBody.create(mediaType, json.toString())
    }

    private fun convertRequestBodyToJson(request: Request): JsonElement? {
        val buffer = Buffer()
        try {
            request.newBuilder().build().body?.let {
                it.writeTo(buffer)
                val body = buffer.readUtf8()
                val parser = JsonParser()
                return parser.parse(body)
            }
        } catch (ignore: IOException) {
        } finally {
            buffer.close()
        }
        return null
    }
}

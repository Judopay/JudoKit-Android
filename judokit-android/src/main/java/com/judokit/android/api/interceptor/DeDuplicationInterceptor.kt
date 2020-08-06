package com.judokit.android.api.interceptor

import com.google.gson.JsonParser
import com.judokit.android.api.error.DuplicateTransactionError
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

private const val PAYMENT_REFERENCE_KEY = "yourPaymentReference"
private const val UNIQUE_REQUEST_KEY = "uniqueRequest"

internal class DeDuplicationInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val body = request.body()

        if (body != null) {

            val parser = JsonParser()

            val bodAsString = bodyToString(body)
            val jsonElement = parser.parse(bodAsString)

            if (jsonElement.isJsonObject) {
                val jsonObject = jsonElement.asJsonObject
                val uniqueReference = jsonObject[PAYMENT_REFERENCE_KEY]
                val uniqueRequest = jsonObject[UNIQUE_REQUEST_KEY]

                if (uniqueRequest != null &&
                    uniqueRequest.asBoolean &&
                    uniqueReference != null &&
                    UNIQUE_RESPONSES.contains(uniqueReference.asString)
                ) {
                    throw DuplicateTransactionError(uniqueReference.asString)
                } else {
                    val response = chain.proceed(request)
                    if (uniqueReference != null) {
                        UNIQUE_RESPONSES.add(uniqueReference.asString)
                    }
                    return response
                }
            }
        }

        return chain.proceed(request)
    }

    @Throws(IOException::class)
    private fun bodyToString(request: RequestBody): String {
        val buffer = Buffer()
        request.writeTo(buffer)
        return buffer.readUtf8()
    }

    companion object {
        private val UNIQUE_RESPONSES: MutableList<String> = ArrayList()
    }
}

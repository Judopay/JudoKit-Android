package com.judopay.judokit.android.api.factory

import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@DisplayName("Testing ApiCallAdapterFactory and ResultCall")
internal class ApiCallAdapterFactoryTest {
    private val factory = ApiCallAdapterFactory()
    private val retrofit = mockk<Retrofit>()

    @Suppress("UNCHECKED_CAST")
    private fun makeParameterizedType(
        raw: Class<*>,
        vararg typeArgs: Type,
    ): ParameterizedType =
        object : ParameterizedType {
            override fun getRawType(): Type = raw

            override fun getActualTypeArguments(): Array<out Type> = typeArgs

            override fun getOwnerType(): Type? = null
        }

    @Test
    @DisplayName("get() returns null for non-Call return type")
    fun getReturnsNullForNonCallType() {
        assertNull(factory.get(String::class.java, emptyArray(), retrofit))
    }

    @Test
    @DisplayName("get() returns ResultAdapter for Call<JudoApiCallResult<T>>")
    fun getReturnsResultAdapterForJudoApiCallResult() {
        val innerType = makeParameterizedType(JudoApiCallResult::class.java, String::class.java)
        val callType = makeParameterizedType(Call::class.java, innerType)
        val adapter = factory.get(callType, emptyArray(), retrofit)
        assertNotNull(adapter)
        assertTrue(adapter is ResultAdapter)
    }

    @Test
    @DisplayName("get() returns null for Call<String> (non-JudoApiCallResult)")
    fun getReturnsNullForNonJudoApiCallResultCall() {
        val callType = makeParameterizedType(Call::class.java, String::class.java)
        assertNull(factory.get(callType, emptyArray(), retrofit))
    }

    @Test
    @DisplayName("ResultAdapter.responseType() returns the inner type")
    fun resultAdapterResponseType() {
        val innerType = makeParameterizedType(JudoApiCallResult::class.java, String::class.java)
        val callType = makeParameterizedType(Call::class.java, innerType)
        val adapter = factory.get(callType, emptyArray(), retrofit) as ResultAdapter
        assertEquals(String::class.java, adapter.responseType())
    }

    @Test
    @DisplayName("ResultAdapter.adapt() wraps call in ResultCall")
    fun resultAdapterAdaptWrapsInResultCall() {
        val call = mockk<Call<Any>>(relaxed = true)
        val innerType = makeParameterizedType(JudoApiCallResult::class.java, Any::class.java)
        val callType = makeParameterizedType(Call::class.java, innerType)
        val adapter = factory.get(callType, emptyArray(), retrofit) as ResultAdapter
        val result = adapter.adapt(call as Call<Type>)
        assertTrue(result is ResultCall<*>)
    }

    @Test
    @DisplayName("ResultCall.cloneImpl() creates a new ResultCall wrapping proxy clone")
    fun resultCallCloneImplCreatesNewResultCall() {
        val proxy = mockk<Call<String>>(relaxed = true)
        val cloneProxy = mockk<Call<String>>(relaxed = true)
        every { proxy.clone() } returns cloneProxy
        val resultCall = ResultCall(proxy)
        val cloned = resultCall.cloneImpl()
        assertTrue(cloned is ResultCall<*>)
    }

    @Test
    @DisplayName("ResultCall.cancel() delegates to proxy")
    fun resultCallCancelDelegatesToProxy() {
        val proxy = mockk<Call<String>>(relaxed = true)
        val resultCall = ResultCall(proxy)
        resultCall.cancel()
        verify { proxy.cancel() }
    }

    @Test
    @DisplayName("ResultCall.request() returns proxy request")
    fun resultCallRequestReturnsProxyRequest() {
        val proxy = mockk<Call<String>>(relaxed = true)
        val request = mockk<Request>()
        every { proxy.request() } returns request
        val resultCall = ResultCall(proxy)
        assertEquals(request, resultCall.request())
    }

    @Test
    @DisplayName("ResultCall.isExecuted returns proxy value")
    fun resultCallIsExecutedReturnsProxy() {
        val proxy = mockk<Call<String>>(relaxed = true)
        every { proxy.isExecuted } returns true
        val resultCall = ResultCall(proxy)
        assertTrue(resultCall.isExecuted())
    }

    @Test
    @DisplayName("ResultCall.isCanceled returns proxy value")
    fun resultCallIsCanceledReturnsProxy() {
        val proxy = mockk<Call<String>>(relaxed = true)
        every { proxy.isCanceled } returns true
        val resultCall = ResultCall(proxy)
        assertTrue(resultCall.isCanceled())
    }

    @Test
    @DisplayName("ResultCall.enqueueImpl calls callback with Success on successful response")
    fun resultCallEnqueueImplSuccessPath() {
        val proxy = mockk<Call<String>>(relaxed = true)
        val resultCall = ResultCall(proxy)
        val responseBody = "test-body"
        var capturedResult: JudoApiCallResult<String>? = null

        val callback =
            object : Callback<JudoApiCallResult<String>> {
                override fun onResponse(
                    call: Call<JudoApiCallResult<String>>,
                    response: Response<JudoApiCallResult<String>>,
                ) {
                    capturedResult = response.body()
                }

                override fun onFailure(
                    call: Call<JudoApiCallResult<String>>,
                    t: Throwable,
                ) { // no-op: test only exercises onResponse path
                }
            }

        every { proxy.enqueue(any()) } answers {
            val innerCallback = firstArg<Callback<String>>()
            innerCallback.onResponse(proxy, Response.success(responseBody))
        }

        resultCall.enqueue(callback)

        assertTrue(capturedResult is JudoApiCallResult.Success)
        assertEquals(responseBody, (capturedResult as JudoApiCallResult.Success).data)
    }

    @Test
    @DisplayName("ResultCall.enqueueImpl calls callback with Failure on network error")
    fun resultCallEnqueueImplFailurePath() {
        val proxy = mockk<Call<String>>(relaxed = true)
        val resultCall = ResultCall(proxy)
        val error = RuntimeException("network error")
        var capturedResult: JudoApiCallResult<String>? = null

        val callback =
            object : Callback<JudoApiCallResult<String>> {
                override fun onResponse(
                    call: Call<JudoApiCallResult<String>>,
                    response: Response<JudoApiCallResult<String>>,
                ) {
                    capturedResult = response.body()
                }

                override fun onFailure(
                    call: Call<JudoApiCallResult<String>>,
                    t: Throwable,
                ) { // no-op: test only exercises onResponse path
                }
            }

        every { proxy.enqueue(any()) } answers {
            val innerCallback = firstArg<Callback<String>>()
            innerCallback.onFailure(proxy, error)
        }

        resultCall.enqueue(callback)

        assertTrue(capturedResult is JudoApiCallResult.Failure)
        assertEquals(error, (capturedResult as JudoApiCallResult.Failure).throwable)
    }

    @Test
    @DisplayName("ResultCall.enqueueImpl calls callback with Failure containing parsed ApiError on HTTP error response")
    fun resultCallEnqueueImplHttpErrorWithErrorBody() {
        val proxy = mockk<Call<String>>(relaxed = true)
        val resultCall = ResultCall(proxy)
        val errorJson = """{"code":1234,"category":1,"message":"Bad request"}"""
        val errorBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())
        var capturedResult: JudoApiCallResult<String>? = null

        val callback =
            object : Callback<JudoApiCallResult<String>> {
                override fun onResponse(
                    call: Call<JudoApiCallResult<String>>,
                    response: Response<JudoApiCallResult<String>>,
                ) {
                    capturedResult = response.body()
                }

                override fun onFailure(
                    call: Call<JudoApiCallResult<String>>,
                    t: Throwable,
                ) { // no-op: test only exercises onResponse path
                }
            }

        every { proxy.enqueue(any()) } answers {
            val innerCallback = firstArg<Callback<String>>()
            innerCallback.onResponse(proxy, Response.error(400, errorBody))
        }

        resultCall.enqueue(callback)

        assertTrue(capturedResult is JudoApiCallResult.Failure)
        val failure = capturedResult as JudoApiCallResult.Failure
        assertEquals(400, failure.statusCode)
        assertNotNull(failure.error)
        assertEquals(1234, failure.error?.code)
    }

    @Test
    @DisplayName("ResultCall.enqueueImpl handles malformed error body gracefully")
    fun resultCallEnqueueImplMalformedErrorBody() {
        val proxy = mockk<Call<String>>(relaxed = true)
        val resultCall = ResultCall(proxy)
        val malformedBody = "not-json".toResponseBody("application/json".toMediaTypeOrNull())
        var capturedResult: JudoApiCallResult<String>? = null

        val callback =
            object : Callback<JudoApiCallResult<String>> {
                override fun onResponse(
                    call: Call<JudoApiCallResult<String>>,
                    response: Response<JudoApiCallResult<String>>,
                ) {
                    capturedResult = response.body()
                }

                override fun onFailure(
                    call: Call<JudoApiCallResult<String>>,
                    t: Throwable,
                ) { // no-op: test only exercises onResponse path
                }
            }

        every { proxy.enqueue(any()) } answers {
            val innerCallback = firstArg<Callback<String>>()
            innerCallback.onResponse(proxy, Response.error(500, malformedBody))
        }

        resultCall.enqueue(callback)

        assertTrue(capturedResult is JudoApiCallResult.Failure)
        val failure = capturedResult as JudoApiCallResult.Failure
        assertEquals(500, failure.statusCode)
        assertNull(failure.error)
    }
}

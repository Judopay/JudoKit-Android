package com.judopay.api.factory

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.judopay.api.error.ApiError
import com.judopay.api.model.response.JudoApiCallResult
import okhttp3.Request
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class CallDelegate<TypeIn, TypeOut>(protected val proxy: Call<TypeIn>) : Call<TypeOut> {

    override fun execute(): Response<TypeOut> = throw NotImplementedError()
    final override fun enqueue(callback: Callback<TypeOut>) = enqueueImpl(callback)
    final override fun clone(): Call<TypeOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TypeOut>)
    abstract fun cloneImpl(): Call<TypeOut>
}

class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, JudoApiCallResult<T>>(proxy) {

    override fun enqueueImpl(callback: Callback<JudoApiCallResult<T>>) = proxy.enqueue(object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val result = if (response.isSuccessful) {
                val body = response.body()
                JudoApiCallResult.Success<T>(body)
            } else {
                val code = response.code()
                var error: ApiError? = null

                response.errorBody()?.charStream()?.let {
                    try {
                        error = Gson().fromJson(it, ApiError::class.java)
                    } catch (exception: JsonSyntaxException) {
                        exception.printStackTrace()
                    }
                }

                JudoApiCallResult.Failure(code, error)
            }

            callback.onResponse(this@ResultCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) {
            val result = JudoApiCallResult.Failure(throwable = throwable)
            callback.onResponse(this@ResultCall, Response.success(result))
        }
    })

    override fun cloneImpl() = ResultCall(proxy.clone())

}

class ResultAdapter(private val type: Type) : CallAdapter<Type, Call<JudoApiCallResult<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<JudoApiCallResult<Type>> = ResultCall(call)
}

class JudoApiCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                JudoApiCallResult::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    ResultAdapter(resultType)
                }
                else -> null
            }
        }
        else -> null
    }
}


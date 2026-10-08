package com.judopay.judokit.android.api

import com.judopay.judokit.android.api.model.response.cdn.DsCertsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Url

internal interface DsCdnApiService {
    @GET
    @Headers("Accept: application/json")
    suspend fun fetchDsCerts(
        @Url url: String,
        @Header("If-None-Match") ifNoneMatch: String?,
        @Header("If-Modified-Since") ifModifiedSince: String?,
    ): Response<DsCertsResponse>
}

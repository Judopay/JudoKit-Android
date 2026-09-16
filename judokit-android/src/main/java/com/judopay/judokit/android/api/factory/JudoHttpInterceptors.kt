package com.judopay.judokit.android.api.factory

import okhttp3.Interceptor

/**
 * Single source of truth for host-supplied OkHttp interceptors (e.g. Chucker for debug
 * inspection) applied to *all* judo SDK HTTP clients — the judo API, the Recommendation API
 * and the DS-certificate CDN.
 *
 * Set this once at app start, before the first network call. Intended for debug builds only;
 * do not ship logging/inspection interceptors to production.
 */
object JudoHttpInterceptors {
    @Volatile
    var interceptors: List<Interceptor> = emptyList()
}

package com.judopay.judokit.android.service

import android.content.Context
import android.util.Log
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.DsCdnApiService
import com.judopay.judokit.android.api.factory.DsCdnApiServiceFactory
import com.judopay.judokit.android.api.model.response.cdn.DsCertEntry
import com.judopay.judokit.android.api.model.response.cdn.DsCertsCache
import com.judopay.judokit.android.api.model.response.cdn.hasNearExpiryEntry
import com.judopay.judokit.android.api.model.response.cdn.isFresh
import com.judopay.judokit.android.api.model.response.cdn.isNotExpired
import com.judopay.judokit.android.api.model.response.cdn.isSupportedSchemaMajor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val CDN_URL = "judokit/ds-certs"
private const val DEFAULT_MAX_AGE_MS = 24 * 60 * 60 * 1000L
private const val PRE_EXPIRY_THRESHOLD_MS = 7 * 24 * 60 * 60 * 1000L
private const val HTTP_NOT_MODIFIED = 304
private const val MILLIS_PER_SECOND = 1000L
private val TAG = DsCertificateRepository::class.java.simpleName

internal class DsCertificateRepository(
    private val api: DsCdnApiService,
    private val cache: DsCertsCacheStore,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val mutex = Mutex()

    /**
     * Best-effort background refresh. Call at payment-screen creation, off the critical path.
     * Never throws — all errors are swallowed and logged.
     */
    suspend fun prefetch() {
        runCatching { refresh() }.onFailure {
            if (it is CancellationException) throw it
            Log.w(TAG, "DS cert prefetch failed", it)
        }
    }

    /**
     * Transaction-path lookup: reads the local cache only, never blocks on the network.
     * Returns null when no valid cached entry exists for [dsId]; the caller must fall back
     * to the 3DS SDK's built-in certificate.
     */
    fun cachedEntry(dsId: String): DsCertEntry? {
        val now = clock()
        return cache.read()?.entries?.firstOrNull { it.dsId == dsId && it.isNotExpired(now) }
    }

    private suspend fun refresh() =
        mutex.withLock {
            // only one refresh runs at a time
            val now = clock()
            val current = cache.read()
            if (current != null &&
                current.isFresh(now) &&
                !current.hasNearExpiryEntry(now, PRE_EXPIRY_THRESHOLD_MS)
            ) {
                return@withLock
            }

            val response =
                api.fetchDsCerts(
                    url = CDN_URL,
                    ifNoneMatch = current?.etag,
                    ifModifiedSince = current?.lastModified,
                )

            when {
                response.code() == HTTP_NOT_MODIFIED ->
                    current?.let { cache.write(it.copy(fetchedAt = now)) }

                response.isSuccessful -> {
                    val body = response.body() ?: return@withLock
                    if (!body.schemaVersion.isSupportedSchemaMajor()) {
                        Log.w(TAG, "Unsupported ds-certs.json schemaVersion '${body.schemaVersion}' — ignoring payload")
                        return@withLock
                    }
                    cache.write(
                        DsCertsCache(
                            etag = response.headers()["ETag"] ?: body.etag,
                            lastModified = response.headers()["Last-Modified"].orEmpty(),
                            fetchedAt = now,
                            maxAgeMs = parseMaxAgeMs(response.headers()["Cache-Control"]),
                            entries = body.entries,
                        ),
                    )
                }

                else ->
                    Log.w(TAG, "DS cert CDN fetch returned ${response.code()} — keeping existing cache")
            }
        }

    private fun parseMaxAgeMs(cacheControl: String?): Long {
        val seconds =
            cacheControl
                ?.let { Regex("""max-age=(\d+)""").find(it) }
                ?.groupValues
                ?.get(1)
                ?.toLongOrNull()
        return seconds?.times(MILLIS_PER_SECOND) ?: DEFAULT_MAX_AGE_MS
    }

    companion object {
        @Volatile
        private var instance: DsCertificateRepository? = null

        fun getInstance(
            context: Context,
            judo: Judo,
        ): DsCertificateRepository =
            instance ?: synchronized(this) {
                instance ?: DsCertificateRepository(
                    api = DsCdnApiServiceFactory.create(context.applicationContext, judo),
                    cache = DsCertsCacheStore(context.applicationContext),
                ).also { instance = it }
            }
    }
}

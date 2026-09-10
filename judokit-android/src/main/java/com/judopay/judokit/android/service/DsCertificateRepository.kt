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
private const val HEADER_ETAG = "ETag"
private const val HEADER_LAST_MODIFIED = "Last-Modified"
private const val HEADER_CACHE_CONTROL = "Cache-Control"
private val TAG = DsCertificateRepository::class.java.simpleName

internal class DsCertificateRepository(
    private val api: DsCdnApiService,
    private val cache: DsCertsCacheStore,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val mutex = Mutex()

    /**
     * Best-effort background refresh. Call at payment-screen creation, off the critical path.
     * All errors are swallowed and logged, except [CancellationException], which is rethrown
     * so coroutine cancellation propagates as normal.
     */
    suspend fun prefetch() {
        runCatching { refresh() }.onFailure {
            if (it is CancellationException) throw it
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

    /**
     * Drops all locally cached DS certificates. The next [prefetch] re-populates the cache from
     * the CDN; until then [cachedEntry] returns null and callers fall back to the 3DS SDK's
     * built-in certificates. Intended for QA/support scenarios that need a forced re-fetch.
     */
    fun clearCache() = cache.clear()

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
                    ifNoneMatch = current?.etag?.ifBlank { null },
                    ifModifiedSince = current?.lastModified?.ifBlank { null },
                )

            when {
                response.code() == HTTP_NOT_MODIFIED ->
                    current?.let { cache.write(it.copy(fetchedAt = now)) }

                response.isSuccessful -> {
                    val body = response.body() ?: return@withLock
                    if (!body.schemaVersion.isSupportedSchemaMajor()) {
                        return@withLock
                    }
                    if (body.entries.isEmpty()) {
                        return@withLock
                    }
                    cache.write(
                        DsCertsCache(
                            etag = response.headers()[HEADER_ETAG] ?: body.etag,
                            lastModified = response.headers()[HEADER_LAST_MODIFIED].orEmpty(),
                            fetchedAt = now,
                            maxAgeMs = parseMaxAgeMs(response.headers()[HEADER_CACHE_CONTROL]),
                            entries = body.entries,
                        ),
                    )
                }

                else -> {
                    // noop
                    // DS cert CDN fetch returned ${response.code()} — keeping existing cache
                }
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

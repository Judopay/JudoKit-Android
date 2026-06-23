package com.judopay.judokit.android.service

import com.judopay.judokit.android.api.DsCdnApiService
import com.judopay.judokit.android.api.model.response.cdn.DsCertEntry
import com.judopay.judokit.android.api.model.response.cdn.DsCertsCache
import com.judopay.judokit.android.api.model.response.cdn.DsCertsResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
@DisplayName("Testing DsCertificateRepository")
internal class DsCertificateRepositoryTest {
    private val api: DsCdnApiService = mockk()
    private val cache: DsCertsCacheStore = mockk(relaxed = true)

    // Nov 14, 2023 22:13:20 UTC
    private var now = 1_700_000_000_000L
    private val clock: () -> Long = { now }

    private lateinit var sut: DsCertificateRepository

    @BeforeEach
    fun setUp() {
        sut = DsCertificateRepository(api, cache, clock)
    }

    @Nested
    @DisplayName("cachedEntry")
    inner class CachedEntryTests {
        @Test
        @DisplayName("returns null when cache is empty")
        fun returnsNullWhenCacheEmpty() {
            every { cache.read() } returns null

            assertNull(sut.cachedEntry("A000000003"))
        }

        @Test
        @DisplayName("returns null when dsId is not in cache")
        fun returnsNullForUnknownDsId() {
            every { cache.read() } returns aCache(entries = listOf(visaEntry()))

            assertNull(sut.cachedEntry("A000000004"))
        }

        @Test
        @DisplayName("returns null for expired entry")
        fun returnsNullForExpiredEntry() {
            // 2020-01-01 is in the past relative to now (Nov 2023)
            val expired = visaEntry(validUntil = "2020-01-01T00:00:00Z")
            every { cache.read() } returns aCache(entries = listOf(expired))

            assertNull(sut.cachedEntry("A000000003"))
        }

        @Test
        @DisplayName("returns entry for valid non-expired cache hit")
        fun returnsEntryForValidCacheHit() {
            val entry = visaEntry(validUntil = "2099-01-01T00:00:00Z")
            every { cache.read() } returns aCache(entries = listOf(entry))

            assertEquals(entry, sut.cachedEntry("A000000003"))
        }

        @Test
        @DisplayName("returns entry when validUntil is absent (no expiry)")
        fun returnsEntryWhenValidUntilAbsent() {
            val entry = visaEntry(validUntil = null)
            every { cache.read() } returns aCache(entries = listOf(entry))

            assertNotNull(sut.cachedEntry("A000000003"))
        }
    }

    @Nested
    @DisplayName("prefetch")
    inner class PrefetchTests {
        @Test
        @DisplayName("does not call API when cache is fresh and no entry is near expiry")
        fun skipsApiCallWhenCacheIsFresh() =
            runTest {
                every { cache.read() } returns aFreshCache()

                sut.prefetch()

                coVerify(exactly = 0) { api.fetchDsCerts(any(), any(), any()) }
            }

        @Test
        @DisplayName("calls API with If-None-Match when cache is stale")
        fun callsApiWithETagWhenCacheStale() =
            runTest {
                every { cache.read() } returns aStaleCache()
                coEvery { api.fetchDsCerts(any(), "v2025-03-01-abc123", any()) } returns notModifiedResponse()

                sut.prefetch()

                coVerify(exactly = 1) { api.fetchDsCerts(any(), "v2025-03-01-abc123", any()) }
            }

        @Test
        @DisplayName("bumps fetchedAt on 304 without replacing entries")
        fun bumpsFetchedAtOn304() =
            runTest {
                val stale = aStaleCache()
                every { cache.read() } returns stale
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns notModifiedResponse()

                now = 9_999_999_999L
                sut.prefetch()

                verify {
                    cache.write(match { it.fetchedAt == 9_999_999_999L && it.entries == stale.entries })
                }
            }

        @Test
        @DisplayName("replaces cache on 200 with valid schema version")
        fun replacesCacheOn200() =
            runTest {
                every { cache.read() } returns aStaleCache()
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns
                    successResponse(body = aResponse(schemaVersion = "1.0"))

                sut.prefetch()

                verify { cache.write(any()) }
            }

        @Test
        @DisplayName("rejects payload with unsupported major schemaVersion")
        fun rejectsUnsupportedSchemaVersion() =
            runTest {
                every { cache.read() } returns aStaleCache()
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns
                    successResponse(body = aResponse(schemaVersion = "2.0"))

                sut.prefetch()

                verify(exactly = 0) { cache.write(any()) }
            }

        @Test
        @DisplayName("does not update cache on non-200 / non-304 response")
        fun doesNotUpdateCacheOnErrorResponse() =
            runTest {
                every { cache.read() } returns aStaleCache()
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns
                    Response.error(500, "server error".toResponseBody())

                sut.prefetch()

                verify(exactly = 0) { cache.write(any()) }
            }

        @Test
        @DisplayName("swallows network exception and does not crash")
        fun swallowsNetworkException() =
            runTest {
                every { cache.read() } returns null
                coEvery { api.fetchDsCerts(any(), any(), any()) } throws RuntimeException("timeout")

                sut.prefetch() // must not throw
            }

        @Test
        @DisplayName("triggers refresh when cache has a near-expiry entry despite fresh TTL")
        fun triggersRefreshForNearExpiryEntry() =
            runTest {
                // Entry expired in 2020 — well within the 7-day pre-expiry window
                val nearExpiry = visaEntry(validUntil = "2020-01-01T00:00:00Z")
                // fetchedAt = now → cache TTL is fresh, but entry is past expiry
                every { cache.read() } returns
                    aCache(fetchedAt = now, maxAgeMs = 86_400_000L, entries = listOf(nearExpiry))
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns
                    successResponse(body = aResponse())

                sut.prefetch()

                coVerify(exactly = 1) { api.fetchDsCerts(any(), any(), any()) }
            }

        @Test
        @DisplayName("concurrent prefetch calls only trigger one network request")
        fun concurrentPrefetchOnlyOneNetworkCall() =
            runTest {
                // Track cache state so the second job sees a fresh cache after the first write
                var stored: DsCertsCache? = null
                every { cache.read() } answers { stored }
                every { cache.write(any()) } answers { stored = firstArg() }
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns
                    successResponse(body = aResponse())

                val job1 = launch { sut.prefetch() }
                val job2 = launch { sut.prefetch() }
                job1.join()
                job2.join()

                coVerify(atMost = 1) { api.fetchDsCerts(any(), any(), any()) }
            }
    }

    @Nested
    @DisplayName("Cache-Control max-age parsing")
    inner class CacheControlTests {
        @Test
        @DisplayName("honours max-age from Cache-Control header")
        fun honoursMaxAgeFromHeader() =
            runTest {
                every { cache.read() } returns aStaleCache()
                var writtenCache: DsCertsCache? = null
                every { cache.write(any()) } answers { writtenCache = firstArg() }
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns
                    successResponse(
                        headers = Headers.headersOf("Cache-Control", "max-age=3600", "ETag", "v2"),
                        body = aResponse(),
                    )

                sut.prefetch()

                assertEquals(3_600_000L, writtenCache?.maxAgeMs)
            }

        @Test
        @DisplayName("falls back to 24 h default when Cache-Control header is absent")
        fun fallsBackTo24hWhenHeaderAbsent() =
            runTest {
                every { cache.read() } returns aStaleCache()
                var writtenCache: DsCertsCache? = null
                every { cache.write(any()) } answers { writtenCache = firstArg() }
                coEvery { api.fetchDsCerts(any(), any(), any()) } returns
                    successResponse(body = aResponse())

                sut.prefetch()

                assertEquals(86_400_000L, writtenCache?.maxAgeMs)
            }
    }

    private fun visaEntry(validUntil: String? = "2099-01-01T00:00:00Z") =
        DsCertEntry(
            dsId = "A000000003",
            dsName = "Visa",
            dsCertificate = "base64pem==",
            rootCertificates = listOf("rootpem=="),
            keyId = "747da056-476c-4296-a7c4-7e853e235ef0",
            validUntil = validUntil,
        )

    private fun aCache(
        etag: String = "v2025-03-01-abc123",
        fetchedAt: Long = now,
        maxAgeMs: Long = 86_400_000L,
        entries: List<DsCertEntry> = listOf(visaEntry()),
    ) = DsCertsCache(
        etag = etag,
        lastModified = "Tue, 01 Mar 2025 00:00:00 GMT",
        fetchedAt = fetchedAt,
        maxAgeMs = maxAgeMs,
        entries = entries,
    )

    // fetchedAt = now → (now - now) = 0 < maxAgeMs → fresh
    private fun aFreshCache() = aCache(fetchedAt = now, maxAgeMs = 86_400_000L)

    // fetchedAt = 0 → (now - 0) >> maxAgeMs → stale
    private fun aStaleCache() = aCache(fetchedAt = 0L, maxAgeMs = 86_400_000L)

    private fun aResponse(schemaVersion: String = "1.0") =
        DsCertsResponse(
            schemaVersion = schemaVersion,
            publishedAt = "2025-03-01T00:00:00Z",
            etag = "v2025-03-01-abc123",
            entries = listOf(visaEntry()),
        )

    private fun successResponse(
        headers: Headers = Headers.headersOf("ETag", "v2025-03-01-abc123"),
        body: DsCertsResponse,
    ): Response<DsCertsResponse> = Response.success(body, headers)

    /** Creates a retrofit2.Response that reports HTTP 304 Not Modified. */
    private fun notModifiedResponse(): Response<DsCertsResponse> =
        mockk {
            every { code() } returns 304
            every { isSuccessful } returns false
            every { body() } returns null
            every { headers() } returns Headers.headersOf()
        }
}

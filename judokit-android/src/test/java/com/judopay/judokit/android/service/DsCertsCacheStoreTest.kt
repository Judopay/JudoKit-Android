package com.judopay.judokit.android.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.api.model.response.cdn.DsCertEntry
import com.judopay.judokit.android.api.model.response.cdn.DsCertsCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing DsCertsCacheStore")
internal class DsCertsCacheStoreTest {
    private val prefs: SharedPreferences = mockk(relaxed = true)
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val gson = GsonBuilder().create()

    private lateinit var sut: DsCertsCacheStore

    @BeforeEach
    fun setUp() {
        every { context.getSharedPreferences(any(), any()) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor

        sut = DsCertsCacheStore(context, gson)
    }

    @Test
    @DisplayName("read returns null when no cached value exists")
    fun readReturnsNullWhenNoCachedValue() {
        every { prefs.getString("cache_v1", null) } returns null

        assertNull(sut.read())
    }

    @Test
    @DisplayName("read returns null when JSON is corrupt")
    fun readReturnsNullForCorruptJson() {
        every { prefs.getString("cache_v1", null) } returns "not-valid-json{{{{"

        assertNull(sut.read())
    }

    @Test
    @DisplayName("write serialises cache and persists to SharedPreferences")
    fun writeSerialisesCacheAndPersists() {
        val jsonSlot = slot<String>()
        every { editor.putString("cache_v1", capture(jsonSlot)) } returns editor

        sut.write(aCache())

        verify { editor.apply() }
        val written = jsonSlot.captured
        assert(written.contains("A000000003"))
        assert(written.contains("v2025-03-01"))
    }

    @Test
    @DisplayName("round-trip write then read returns equal cache")
    fun roundTripWriteThenRead() {
        val original = aCache()
        var stored: String? = null

        every { editor.putString("cache_v1", any()) } answers {
            stored = secondArg()
            editor
        }
        every { prefs.getString("cache_v1", null) } answers { stored }

        sut.write(original)
        val restored = sut.read()

        assertNotNull(restored)
        assertEquals(original, restored)
    }

    private fun aCache() =
        DsCertsCache(
            etag = "v2025-03-01-abc123",
            lastModified = "Tue, 01 Mar 2025 00:00:00 GMT",
            fetchedAt = 1_000_000L,
            maxAgeMs = 86_400_000L,
            entries =
                listOf(
                    DsCertEntry(
                        dsId = "A000000003",
                        dsName = "Visa",
                        dsCertificate = "base64pem==",
                        rootCertificates = listOf("rootpem=="),
                        keyId = "747da056-476c-4296-a7c4-7e853e235ef0",
                        validUntil = "2026-03-01T00:00:00Z",
                    ),
                ),
        )
}

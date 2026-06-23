package com.judopay.judokit.android.service

import com.judopay.judo3ds2.certificate.Algorithm
import com.judopay.judokit.android.api.model.response.cdn.DsCertEntry
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Testing DsCertEntryExtensions")
internal class DsCertEntryExtensionsTest {
    private fun entry(certificate: String = RSA_CERT_PEM) =
        DsCertEntry(
            dsId = "A000000003",
            dsName = "Visa",
            dsCertificate = certificate,
            rootCertificates = listOf("rootpem=="),
            keyId = "747da056-476c-4296-a7c4-7e853e235ef0",
            validUntil = "2099-01-01T00:00:00Z",
        )

    @Nested
    @DisplayName("toMaterial")
    inner class ToMaterialTests {
        @Test
        @DisplayName("maps fields and derives RSA from an RSA certificate")
        fun mapsRsaEntry() {
            val material = entry(RSA_CERT_PEM).toMaterial()
            assertNotNull(material)
            assertEquals("747da056-476c-4296-a7c4-7e853e235ef0", material!!.keyId)
            assertEquals(RSA_CERT_PEM, material.publicKeyPem)
            assertEquals(Algorithm.RSA, material.algorithm)
            assertEquals(listOf("rootpem=="), material.rootCertificatesPem)
        }

        @Test
        @DisplayName("derives EC from an EC certificate")
        fun mapsEcEntry() {
            val material = entry(EC_CERT_PEM).toMaterial()
            assertNotNull(material)
            assertEquals(Algorithm.EC, material!!.algorithm)
        }

        @Test
        @DisplayName("returns null when the certificate material cannot be parsed")
        fun returnsNullForUnparseableCertificate() {
            assertNull(entry("not-a-certificate").toMaterial())
        }
    }

    @Nested
    @DisplayName("JudoDsCertificateProvider")
    inner class JudoDsCertificateProviderTests {
        @Test
        @DisplayName("returns material when cache has a valid entry")
        fun returnsMaterialOnCacheHit() {
            val repo: DsCertificateRepository = mockk()
            every { repo.cachedEntry("A000000003") } returns entry(RSA_CERT_PEM)

            assertNotNull(JudoDsCertificateProvider(repo).certificate("A000000003"))
        }

        @Test
        @DisplayName("returns null when cache has no entry for dsId")
        fun returnsNullOnCacheMiss() {
            val repo: DsCertificateRepository = mockk()
            every { repo.cachedEntry(any()) } returns null

            assertNull(JudoDsCertificateProvider(repo).certificate("A000000003"))
        }

        @Test
        @DisplayName("returns null when the cached entry's certificate cannot be parsed")
        fun returnsNullForUnparseableCertificate() {
            val repo: DsCertificateRepository = mockk()
            every { repo.cachedEntry(any()) } returns entry("not-a-certificate")

            assertNull(JudoDsCertificateProvider(repo).certificate("A000000003"))
        }
    }

    private companion object {
        val RSA_CERT_PEM =
            """
            -----BEGIN CERTIFICATE-----
            MIICqjCCAZICCQDzSXtV7rVYwzANBgkqhkiG9w0BAQsFADAWMRQwEgYDVQQDDAtU
            ZXN0IFJTQSBEUzAgFw0yNjA2MTkyMzAyNTlaGA8yMTI2MDUyNjIzMDI1OVowFjEU
            MBIGA1UEAwwLVGVzdCBSU0EgRFMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEK
            AoIBAQCtZvg9B3x6A1JucTOSi5YoneJMHsgak57dHX11OiPeDDJdqMSrsMSB3c/M
            /pqZ3H9NWC4vGax63vmUJQXTxXcrtoS0TcSfdvZe8tMO5ak/IvlXuHk11y3qiM33
            +/2zGoGhoEW29nUI+ykYBLTFXcbi79Fxch6IOSswt928x0qoanMlXeCUH21icBQf
            sZXr59X5CPwiYi1PSTCeSgnf6KYCLyfATG4sTZScrFc1E//uJZ9WtEVifiT6S7v7
            gyJBWc+3YYkvjOE5JmzAwg4xHMdaXKHFOA7YQhYX++BxUp9ga3KiQyh5XKtkJ+wQ
            QW1reekddzt5k0GSmScxBVDYbAEdAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAE21
            UFwQe+8SghGFNb4rC9Kq68vvLWNPpKDY9t/m7zTyxYhlnOVTqAO5mvgVYoD/KuqN
            CY/MTCo7/ymS+OsPnpjkqdSd1QbI27+zySEPAz4A0ENISmwVQsYOUA4p2tqMeq7o
            PkFDL0ZsRHL9P3PjbhyMIw0Sqv+g8AKTdNrecQJ4mPkTUygj3gz2cg/k/jy/vLcl
            Apw991MwOS16ftKlUmnu+y8NPMXtn/DuwQ6F7lN6N6eFjjLZdgCPsIex6crgPGlY
            tNT6bTSzrYCiqoK3mN91moMoQvUeL/8OcqtcBQh7BMaNbeylPMucAT6UZx9lDXvN
            y1L577h4oVJILCoZeS0=
            -----END CERTIFICATE-----
            """.trimIndent()

        val EC_CERT_PEM =
            """
            -----BEGIN CERTIFICATE-----
            MIIBGjCBwgIJALPuv2RtTJOBMAoGCCqGSM49BAMCMBUxEzARBgNVBAMMClRlc3Qg
            RUMgRFMwIBcNMjYwNjE5MjMwMzE5WhgPMjEyNjA1MjYyMzAzMTlaMBUxEzARBgNV
            BAMMClRlc3QgRUMgRFMwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQlzf+gpaIA
            uG2GTyg+mUfd5SscY7itmVboH9A44e6O5fHQ+eYopDca2RJbbivj4IEiXJZePlKf
            rQtRZ/2Hz75oMAoGCCqGSM49BAMCA0cAMEQCID1dgEJcIugQZ29hXdyXMZgtLohT
            IIDOQCkm7lsUjov4AiAo2BAFdYRb0i8dKzHB8omiZPmKhG9WcSURpFMyThEp0A==
            -----END CERTIFICATE-----
            """.trimIndent()
    }
}

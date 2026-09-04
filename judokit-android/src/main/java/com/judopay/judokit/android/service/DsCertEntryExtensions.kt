package com.judopay.judokit.android.service

import android.util.Base64
import com.judopay.judo3ds2.certificate.Algorithm
import com.judopay.judo3ds2.certificate.DirectoryServerCertificateProvider
import com.judopay.judo3ds2.certificate.DsCertificateMaterial
import com.judopay.judokit.android.api.model.response.cdn.DsCertEntry
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.X509EncodedKeySpec

internal fun DsCertEntry.toMaterial(): DsCertificateMaterial? {
    val alg = dsCertificate.deriveDsAlgorithm() ?: return null
    return DsCertificateMaterial(
        publicKeyPem = dsCertificate,
        keyId = keyId,
        algorithm = alg,
        rootCertificatesPem = rootCertificates,
    )
}

/**
 * Derives the AReq encryption [Algorithm] from the DS public-key material so the CDN payload does
 * not have to carry (and keep consistent) a redundant `algorithm` field. The encryption scheme is
 * a pure function of the key type — an RSA key implies RSA-OAEP, an EC key implies ECDH-ES.
 *
 * Mirrors the SDK's own parse paths (`OverrideCertificatePublicKeyProvider`): a full X.509
 * certificate exposes its key type directly, while a bare `SubjectPublicKeyInfo` is matched against
 * the supported key factories. Returns `null` when the material cannot be parsed or the key type is
 * unsupported, in which case [toMaterial] yields `null` and the caller falls back to the SDK's
 * built-in certificate.
 */
private fun String.deriveDsAlgorithm(): Algorithm? {
    val trimmed = trim()
    return if (trimmed.startsWith("-----BEGIN CERTIFICATE-----")) {
        trimmed.certificateKeyAlgorithm()
    } else {
        trimmed.rawKeyAlgorithm()
    }
}

private fun String.certificateKeyAlgorithm(): Algorithm? =
    runCatching {
        val cert =
            CertificateFactory
                .getInstance("X.509")
                .generateCertificate(byteInputStream()) as X509Certificate
        cert.publicKey.algorithm.toDsAlgorithm()
    }.getOrNull()

/**
 * A bare `SubjectPublicKeyInfo` carries its algorithm OID in the DER, but the JDK's [KeyFactory]
 * needs the key type up front — so probe the supported types and keep the one that parses.
 */
private fun String.rawKeyAlgorithm(): Algorithm? {
    val der = stripPemArmorToDer() ?: return null
    return Algorithm.entries.firstOrNull { alg ->
        runCatching {
            KeyFactory.getInstance(alg.name).generatePublic(X509EncodedKeySpec(der))
        }.isSuccess
    }
}

private fun String.stripPemArmorToDer(): ByteArray? =
    runCatching {
        val body =
            lineSequence()
                .filterNot { it.startsWith("-----BEGIN") || it.startsWith("-----END") }
                .joinToString(separator = "")
                .trim()
        Base64.decode(body, Base64.DEFAULT)
    }.getOrNull()

private fun String.toDsAlgorithm(): Algorithm? =
    when (uppercase()) {
        "RSA" -> Algorithm.RSA
        "EC", "ECDSA" -> Algorithm.EC
        else -> null
    }

internal class JudoDsCertificateProvider(
    private val repo: DsCertificateRepository,
) : DirectoryServerCertificateProvider {
    override fun certificate(directoryServerId: String): DsCertificateMaterial? = repo.cachedEntry(directoryServerId)?.toMaterial()
}

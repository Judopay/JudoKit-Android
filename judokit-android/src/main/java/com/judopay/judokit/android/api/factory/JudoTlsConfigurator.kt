package com.judopay.judokit.android.api.factory

import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Single source of truth for the TLS 1.2 socket configuration and the JudoPay certificate
 * pinning applied to judo SDK HTTP clients that talk to `*.judopay.com` (the judo API and the
 * DS-certificate CDN).
 *
 * Keeping the pins in one place ensures every judopay.com client trusts exactly the same leaf
 * keys, so a pin rotation is a single edit.
 */
internal object JudoTlsConfigurator {
    private const val HOSTNAME_WILDCARD_PATTERN = "*.judopay.com"
    private const val PIN_PRIMARY = "sha256/SuY75QgkSNBlMtHNPeW9AayE7KNDAypMBHlJH9GEhXs="
    private const val PIN_BACKUP = "sha256/c4zbAoMygSbepJKqU3322FvFv5unm+TWZROW3FHU1o8="

    /**
     * Restricts [builder] to TLS 1.2 and pins the JudoPay certificate chain.
     *
     * @throws IllegalStateException if the platform exposes an unexpected default trust manager.
     */
    fun applyTls12WithPinning(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, null, null)

        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)

        val trustManagers = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers.first() !is X509TrustManager)) {
            "Unexpected default trust managers: ${trustManagers.contentToString()}"
        }

        val trustManager = trustManagers.first() as X509TrustManager

        val specs =
            listOf(
                ConnectionSpec
                    .Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build(),
            )

        return builder
            .sslSocketFactory(Tls12SslSocketFactory(sslContext.socketFactory), trustManager)
            .connectionSpecs(specs)
            .certificatePinner(
                CertificatePinner
                    .Builder()
                    .add(HOSTNAME_WILDCARD_PATTERN, PIN_PRIMARY, PIN_BACKUP)
                    .build(),
            )
    }
}

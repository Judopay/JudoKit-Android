package com.judopay.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.R;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.CertificatePinner;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Factory that provides the {@link JudoApiService} used for performing all HTTP requests to the
 * judoPay APIs. As creating the JudoApiService requires lots of setup, it is better to use a shared
 * instance than create a new instance per request, so this class ensures that only one instance is
 * used in the application.
 */
public class JudoApiServiceFactory {

    private static final String PARTNER_API_SANDBOX_HOST = "partnerapi.judopay-sandbox.com";
    private static final String PARTNER_API_LIVE_HOST = "partnerapi.judopay.com";

    private static final String CERTIFICATE_1 = "sha1/SSAG1hz7m8LI/eapL/SSpd5o564=";
    private static final String CERTIFICATE_2 = "sha1/o5OZxATDsgmwgcIfIWIneMJ0jkw=";

    /**
     * @param context      the calling Context
     * @param uiClientMode the UI Client Mode that is being used, either Custom UI or the provided Judo SDK UI
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the judoPay REST API.
     */
    public static JudoApiService createApiService(Context context, @Judo.UiClientMode int uiClientMode) {
        return createRetrofit(context.getApplicationContext(), uiClientMode).create(JudoApiService.class);
    }

    private static Retrofit createRetrofit(Context context, @Judo.UiClientMode int uiClientMode) {
        return new Retrofit.Builder()
                .addConverterFactory(getGsonConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(Judo.getApiEnvironmentHost(context))
                .client(getOkHttpClient(uiClientMode, context))
                .build();
    }

    private static OkHttpClient getOkHttpClient(@Judo.UiClientMode int uiClientMode, Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (Judo.isSslPinningEnabled()) {
            builder.certificatePinner(getCertificatePinner());
        }

        setTimeouts(builder);
        setSslSocketFactory(builder, context);
        setInterceptors(builder, uiClientMode, context);

        return builder.build();
    }

    private static void setInterceptors(OkHttpClient.Builder client, @Judo.UiClientMode int uiClientMode, Context context) {
        List<Interceptor> interceptors = client.interceptors();

        interceptors.add(new DeDuplicationInterceptor());
        interceptors.add(new JudoShieldInterceptor(context));
        interceptors.add(new ApiHeadersInterceptor(ApiCredentials.fromConfiguration(context), uiClientMode));
    }

    private static GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create(getGson());
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateJsonDeserializer())
                .registerTypeAdapter(BigDecimal.class, new FormattedBigDecimalDeserializer())
                .create();
    }

    @NonNull
    private static CertificatePinner getCertificatePinner() {
        return new CertificatePinner.Builder()
                .add(PARTNER_API_SANDBOX_HOST, CERTIFICATE_1)
                .add(PARTNER_API_SANDBOX_HOST, CERTIFICATE_2)
                .add(PARTNER_API_LIVE_HOST, CERTIFICATE_1)
                .add(PARTNER_API_LIVE_HOST, CERTIFICATE_2)
                .build();
    }

    private static void setSslSocketFactory(OkHttpClient.Builder builder, Context context) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            if (Judo.getEnvironment() == Judo.UAT) {
                initializeUatEnvironmentSslContext(context, sslContext);
            } else {
                sslContext.init(null, null, null);
            }

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(new TlsSslSocketFactory(socketFactory));
        } catch (CertificateException | IOException | KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeUatEnvironmentSslContext(Context context, SSLContext sslContext) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // loading CAs from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream cert = context.getResources().openRawResource(R.raw.judo_uat);
        Certificate ca;

        try {
            ca = cf.generateCertificate(cert);
        } finally {
            cert.close();
        }

        // creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // creating a TrustManager that trusts the CAs in our KeyStore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        sslContext.init(null, tmf.getTrustManagers(), null);
    }

    private static void setTimeouts(OkHttpClient.Builder builder) {
        builder.connectTimeout(5, SECONDS)
                .readTimeout(3, MINUTES)
                .writeTimeout(30, SECONDS);
    }

}
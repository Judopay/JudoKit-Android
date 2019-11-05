package com.judopay.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.judopay.BuildConfig;
import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.model.Address;
import com.judopay.model.PrimaryAccountDetails;

import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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
    private static final String HOSTNAME_LIVE = "gw1.judopay.com";
    private static final String HOSTNAME_SANDBOX = "gw1.judopay-sandbox.com";

    /**
     * @param context      the calling Context
     * @param uiClientMode the UI Client Mode that is being used, either Custom UI or the provided Judo SDK UI
     * @param judo         the judo instance
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the judoPay REST API.
     */
    public static JudoApiService createApiService(final Context context, @Judo.UiClientMode final int uiClientMode, final Judo judo) {
        return createRetrofit(context.getApplicationContext(), uiClientMode, judo)
                .create(JudoApiService.class);
    }

    private static Retrofit createRetrofit(final Context context, @Judo.UiClientMode final int uiClientMode, final Judo judo) {
        return new Retrofit.Builder()
                .addConverterFactory(getGsonConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(judo.getApiEnvironmentHost(context))
                .client(getOkHttpClient(uiClientMode, context, judo))
                .build();
    }

    private static GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create(getGson());
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Address.class, new Address.Serializer())
                .registerTypeAdapter(Date.class, new DateJsonDeserializer())
                .registerTypeAdapter(BigDecimal.class, new FormattedBigDecimalDeserializer())
                .create();
    }

    private static OkHttpClient getOkHttpClient(@Judo.UiClientMode final int uiClientMode, final Context context, final Judo judo) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers: " + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            List<ConnectionSpec> specs = new ArrayList<>();
            specs.add(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build());

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(new Tls12SslSocketFactory(sslContext.getSocketFactory()), trustManager)
                    .connectionSpecs(specs);

            switch (judo.getEnvironment()) {
                case Judo.LIVE:
                    builder.certificatePinner(new CertificatePinner.Builder()
                            .add(HOSTNAME_LIVE, "sha256/SuY75QgkSNBlMtHNPeW9AayE7KNDAypMBHlJH9GEhXs=", "sha256/c4zbAoMygSbepJKqU3322FvFv5unm+TWZROW3FHU1o8=")
                            .build());
                    break;
                case Judo.SANDBOX:
                    builder.certificatePinner(new CertificatePinner.Builder()
                            .add(HOSTNAME_SANDBOX, "sha256/mpCgFwbYmjH0jpQ3EruXVo+/S73NOAtPeqtGJE8OdZ0=", "sha256/SRjoMmxuXogV8jKdDUKPgRrk9YihOLsrx7ila3iDns4=")
                            .build());
                    break;
                case Judo.CUSTOM:
                    break;
            }

            setTimeouts(builder);
            setInterceptors(builder, uiClientMode, context, judo);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setTimeouts(final OkHttpClient.Builder builder) {
        builder.connectTimeout(5, SECONDS)
                .readTimeout(3, MINUTES)
                .writeTimeout(30, SECONDS);
    }

    private static void setInterceptors(final OkHttpClient.Builder client, @Judo.UiClientMode final int uiClientMode, final Context context, final Judo judo) {
        List<Interceptor> interceptors = client.interceptors();

        interceptors.add(new DeDuplicationInterceptor());
        interceptors.add(new DeviceDnaInterceptor(context));
        interceptors.add(new ApiHeadersInterceptor(ApiCredentials.fromConfiguration(context, judo), uiClientMode, context));
        interceptors.add(new PayLoadInterceptor(context));

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        interceptors.add(loggingInterceptor);
    }
}

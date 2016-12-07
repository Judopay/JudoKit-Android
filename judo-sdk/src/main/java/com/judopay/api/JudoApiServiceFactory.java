package com.judopay.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.error.SslInitializationError;
import com.judopay.model.Address;

import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

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

    /**
     * @param context      the calling Context
     * @param uiClientMode the UI Client Mode that is being used, either Custom UI or the provided Judo SDK UI
     * @param judo         the judo instance
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the judoPay REST API.
     */
    public static JudoApiService createApiService(Context context, @Judo.UiClientMode int uiClientMode, Judo judo) {
        return createRetrofit(context.getApplicationContext(), uiClientMode, judo)
                .create(JudoApiService.class);
    }

    private static Retrofit createRetrofit(Context context, @Judo.UiClientMode int uiClientMode, Judo judo) {
        return new Retrofit.Builder()
                .addConverterFactory(getGsonConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(judo.getApiEnvironmentHost(context))
                .client(getOkHttpClient(uiClientMode, context, judo))
                .build();
    }

    private static OkHttpClient getOkHttpClient(@Judo.UiClientMode int uiClientMode, Context context, Judo judo) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        setTimeouts(builder);
        setSslSocketFactory(builder, context, judo);
        setInterceptors(builder, uiClientMode, context, judo);

        return builder.build();
    }

    private static void setInterceptors(OkHttpClient.Builder client, @Judo.UiClientMode int uiClientMode, Context context, Judo judo) {
        List<Interceptor> interceptors = client.interceptors();

        interceptors.add(new DeDuplicationInterceptor());
        interceptors.add(new DeviceDnaInterceptor(context, judo.getDeviceId()));
        interceptors.add(new ApiHeadersInterceptor(ApiCredentials.fromConfiguration(context, judo), uiClientMode));
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

    private static void setSslSocketFactory(OkHttpClient.Builder builder, Context context, Judo judo) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(new TlsSslSocketFactory(socketFactory));
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new SslInitializationError(e);
        }
    }

    private static void setTimeouts(OkHttpClient.Builder builder) {
        builder.connectTimeout(5, SECONDS)
                .readTimeout(3, MINUTES)
                .writeTimeout(30, SECONDS);
    }
}
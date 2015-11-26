package com.judopay.arch.api;

import com.google.gson.GsonBuilder;
import com.judopay.JudoPay;
import com.judopay.auth.ApiHeadersInterceptor;
import com.judopay.auth.AuthorizationEncoder;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RetrofitFactory {

    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = createRetrofit();
        }
        return retrofit;
    }

    private static Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .addConverterFactory(getGsonConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(JudoPay.getApiEnvironmentHost())
                .client(getOkHttpClient())
                .build();
    }

    private static GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create(new GsonBuilder()
                        .registerTypeAdapter(Date.class, new DateJsonDeserializer())
                        .registerTypeAdapter(Float.class, new FormattedFloatDeserializer())
                        .create()
        );
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient client = new OkHttpClient();

        setTimeouts(client);
        setSslSocketFactory(client);
        setSslPinning(client);

        AuthorizationEncoder authorizationEncoder = new AuthorizationEncoder();
        ApiHeadersInterceptor interceptor = new ApiHeadersInterceptor(authorizationEncoder);

        client.interceptors()
                .add(interceptor);

        return client;
    }

    private static void setSslPinning(OkHttpClient client) {
        if (JudoPay.isSslPinningEnabled()) {
            client.setCertificatePinner(new CertificatePinner.Builder()
                    .add("partnerapi.judopay-sandbox.com", "sha1/SSAG1hz7m8LI/eapL/SSpd5o564=")
                    .add("partnerapi.judopay-sandbox.com", "sha1/o5OZxATDsgmwgcIfIWIneMJ0jkw=")
                    .add("partnerapi.judopay.com", "sha1/SSAG1hz7m8LI/eapL/SSpd5o564=")
                    .add("partnerapi.judopay.com", "sha1/o5OZxATDsgmwgcIfIWIneMJ0jkw=")
                    .build());
        }
    }

    private static void setSslSocketFactory(OkHttpClient client) {
        try {
            client.setSslSocketFactory(new TlsSslSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setTimeouts(OkHttpClient client) {
        client.setConnectTimeout(30, SECONDS);
        client.setReadTimeout(30, SECONDS);
        client.setWriteTimeout(30, SECONDS);
    }

}
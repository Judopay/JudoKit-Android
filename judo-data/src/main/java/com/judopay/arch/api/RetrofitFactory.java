package com.judopay.arch.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.judopay.JudoPay;
import com.judopay.auth.ApiHeadersInterceptor;
import com.judopay.auth.AuthorizationEncoder;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RetrofitFactory {

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            retrofit = createRetrofit(context.getApplicationContext());
        }
        return retrofit;
    }

    @NonNull
    private static Retrofit createRetrofit(Context context) {
        AuthorizationEncoder authorizationEncoder = new AuthorizationEncoder();
        ApiHeadersInterceptor interceptor = new ApiHeadersInterceptor(authorizationEncoder);

        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(30, SECONDS);
        client.setReadTimeout(30, SECONDS);
        client.setWriteTimeout(30, SECONDS);

        if (JudoPay.isSslPinningEnabled()) {
            client.setCertificatePinner(new CertificatePinner.Builder()
                    .add("partnerapi.judopay-sandbox.com", "sha1/SSAG1hz7m8LI/eapL/SSpd5o564=")
                    .add("partnerapi.judopay-sandbox.com", "sha1/o5OZxATDsgmwgcIfIWIneMJ0jkw=")
                    .add("partnerapi.judopay.com", "sha1/SSAG1hz7m8LI/eapL/SSpd5o564=")
                    .add("partnerapi.judopay.com", "sha1/o5OZxATDsgmwgcIfIWIneMJ0jkw=")
                    .build());
        }

        client.interceptors()
                .add(interceptor);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateJsonDeserializer())
                .registerTypeAdapter(Float.class, new FormattedFloatDeserializer())
                .registerTypeAdapter(ClientDetails.class, new ClientDetailsSerializer(context))
                .create();

        GsonConverterFactory converterFactory = GsonConverterFactory.create(gson);

        return new Retrofit.Builder()
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(JudoPay.getApiEnvironmentHost())
                .client(client)
                .build();
    }

}
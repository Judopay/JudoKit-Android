package com.judopay.arch.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.judopay.JudoApiService;
import com.judopay.JudoPay;
import com.judopay.model.ClientDetails;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Factory that provides the {@link JudoApiService} used for performing all HTTP requests to the
 * judoPay APIs. As creating the JudoApiService requires lots of setup, it is better to use a shared
 * instance than create a new instance per request, so this class ensures that only one instance is
 * used in the application.
 */
public class ApiServiceFactory {

    private static Retrofit retrofit;

    /**
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the judoPay API.
     * @param context
     */
    public static JudoApiService getApiService(Context context) {
        return getInstance(context).create(JudoApiService.class);
    }

    private static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            retrofit = createRetrofit(context);
        }
        return retrofit;
    }

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
                .registerTypeAdapter(ClientDetails.class, new ClientDetailsSerializer(context.getApplicationContext()))
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
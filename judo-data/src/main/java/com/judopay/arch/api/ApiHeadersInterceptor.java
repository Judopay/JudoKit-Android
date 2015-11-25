package com.judopay.arch.api;

import android.os.Build;

import com.judopay.rest.BuildConfig;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

class ApiHeadersInterceptor implements Interceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String API_VERSION_HEADER = "Api-Version";
    private static final String CACHE_CONTROL_HEADER = "Cache-Control";

    private static final String JSON_MIME_TYPE = "application/json";
    private static final String API_VERSION = "4.1.0";
    private static final String CACHE_CONTROL = "no-cache";
    private static final String SDK_VERSION_HEADER = "Sdk-Version";
    private static final String USER_AGENT_HEADER = "User-Agent";

    private final UserAgent userAgent;
    private final AuthorizationEncoder authorizationEncoder;

    public ApiHeadersInterceptor(AuthorizationEncoder authorizationEncoder) {
        this.authorizationEncoder = authorizationEncoder;
        this.userAgent = new UserAgent(BuildConfig.VERSION_NAME, Build.VERSION.RELEASE,
                Build.MANUFACTURER, Build.MODEL, Locale.getDefault().getDisplayName());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder()
                .headers(getHeaders());

        return chain.proceed(builder.build());
    }

    private Headers getHeaders() {
        HashMap<String, String> headers = new HashMap<>();

        headers.put(AUTHORIZATION_HEADER, authorizationEncoder.getAuthorization());
        headers.put(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
        headers.put(ACCEPT_HEADER, JSON_MIME_TYPE);
        headers.put(API_VERSION_HEADER, API_VERSION);
        headers.put(CACHE_CONTROL_HEADER, CACHE_CONTROL);
        headers.put(SDK_VERSION_HEADER, BuildConfig.VERSION_NAME);
        headers.put(USER_AGENT_HEADER, userAgent.toString());

        return Headers.of(headers);
    }
}

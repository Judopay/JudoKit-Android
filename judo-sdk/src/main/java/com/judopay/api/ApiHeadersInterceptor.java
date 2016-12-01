package com.judopay.api;

import com.judopay.BuildConfig;
import com.judopay.Judo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;

class ApiHeadersInterceptor implements Interceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String API_VERSION_HEADER = "Api-Version";
    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String UI_MODE_HEADER = "UI-Client-Mode";
    private static final String SDK_VERSION_HEADER = "Sdk-Version";
    private static final String USER_AGENT_HEADER = "User-Agent";

    private static final String JSON_MIME_TYPE = "application/json";
    private static final String API_VERSION = "5.2.0.0";
    private static final String CACHE_CONTROL = "no-cache";
    private static final String JUDO_SDK_UI_MODE = "Judo-SDK";
    private static final String CUSTOM_UI_MODE = "Custom-UI";

    private final UserAgent userAgent;
    private final int uiClientMode;
    private final ApiCredentials apiCredentials;

    public ApiHeadersInterceptor(ApiCredentials apiCredentials, @Judo.UiClientMode int uiClientMode) {
        this.apiCredentials = apiCredentials;
        this.uiClientMode = uiClientMode;
        this.userAgent = new UserAgent(Locale.getDefault().getDisplayName());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request.Builder builder = chain.request()
                .newBuilder()
                .headers(getHeaders());

        return chain.proceed(builder.build());
    }

    private Headers getHeaders() {
        HashMap<String, String> headers = new HashMap<>();

        headers.put(AUTHORIZATION_HEADER, apiCredentials.getBasicAuthorizationHeader());
        headers.put(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
        headers.put(ACCEPT_HEADER, JSON_MIME_TYPE);
        headers.put(API_VERSION_HEADER, API_VERSION);
        headers.put(CACHE_CONTROL_HEADER, CACHE_CONTROL);
        headers.put(SDK_VERSION_HEADER, "Android-" + BuildConfig.VERSION_NAME);
        headers.put(USER_AGENT_HEADER, userAgent.toString());
        headers.put(UI_MODE_HEADER, uiClientMode == Judo.UI_CLIENT_MODE_JUDO_SDK ? JUDO_SDK_UI_MODE : CUSTOM_UI_MODE);

        return Headers.of(headers);
    }
}

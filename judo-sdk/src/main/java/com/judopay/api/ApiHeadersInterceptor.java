package com.judopay.api;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.judopay.BuildConfig;
import com.judopay.Judo;
import com.judopay.arch.AppMetaDataReader;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;

import static com.judopay.arch.TextUtil.isEmpty;

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
    private static final String API_VERSION = "5.6.0.0";
    private static final String CACHE_CONTROL = "no-cache";
    private static final String JUDO_SDK_UI_MODE = "Judo-SDK";
    private static final String CUSTOM_UI_MODE = "Custom-UI";

    private final int uiClientMode;
    private final ApiCredentials apiCredentials;
    private final AppMetaDataReader appMetaDataReader;

    ApiHeadersInterceptor(final ApiCredentials apiCredentials, @Judo.UiClientMode final int uiClientMode, final Context context) {
        this.apiCredentials = apiCredentials;
        this.uiClientMode = uiClientMode;
        this.appMetaDataReader = new AppMetaDataReader(context);
    }

    @Override
    public Response intercept(final @NonNull Chain chain) throws IOException {
        okhttp3.Request.Builder builder = chain.request()
                .newBuilder()
                .headers(getHeaders());

        return chain.proceed(builder.build());
    }

    private Headers getHeaders() {
        return new Headers.Builder()
                .add(AUTHORIZATION_HEADER, apiCredentials.getBasicAuthorizationHeader())
                .add(CONTENT_TYPE_HEADER, JSON_MIME_TYPE)
                .add(ACCEPT_HEADER, JSON_MIME_TYPE)
                .add(API_VERSION_HEADER, API_VERSION)
                .add(CACHE_CONTROL_HEADER, CACHE_CONTROL)
                .add(SDK_VERSION_HEADER, "Android-" + BuildConfig.VERSION_NAME)
                .addUnsafeNonAscii(USER_AGENT_HEADER, getUserAgent())
                .add(UI_MODE_HEADER, uiClientMode == Judo.UI_CLIENT_MODE_JUDO_SDK ? JUDO_SDK_UI_MODE : CUSTOM_UI_MODE)
                .build();
    }

    private String getUserAgent() {
        return String.format(Locale.ENGLISH, "Android/%1$s %2$s%3$s %4$s/%5$s", BuildConfig.VERSION_NAME,
                trim(Build.MANUFACTURER), trim(Build.MODEL), trim(appMetaDataReader.getAppName()), trim(appMetaDataReader.getAppVersion()));
    }

    private String trim(final String text) {
        if (!isEmpty(text)) {
            return text.replaceAll("\\s", "");
        }
        return "";
    }
}

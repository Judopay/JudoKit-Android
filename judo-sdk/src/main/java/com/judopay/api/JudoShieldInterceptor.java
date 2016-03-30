package com.judopay.api;

import android.content.Context;

import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okio.Buffer;

class JudoShieldInterceptor implements Interceptor {

    private final Context context;

    JudoShieldInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        if (request.body() != null) {
            String body = bodyToString(request.body());

            JsonParser parser = new JsonParser();

            if (parser.parse(body).isJsonObject()) {
                try {
                    JSONObject json = new JSONObject(body);
                } catch (JSONException ignore) { }
            } else {
                chain.proceed(request);
            }

        }
        return chain.proceed(request);
    }

    private String bodyToString(final okhttp3.RequestBody request) throws IOException {
        final Buffer buffer = new Buffer();
        request.writeTo(buffer);
        return buffer.readUtf8();
    }

}
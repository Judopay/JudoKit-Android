package com.judopay.api;

import android.content.Context;

import com.google.gson.JsonParser;
import com.judopay.shield.JudoShield;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

class JudoShieldInterceptor implements Interceptor {

    private final JudoShield judoShield;

    JudoShieldInterceptor(Context context) {
        this.judoShield = new JudoShield(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        if (request.body() != null && "POST".equals(request.method())) {
            String body = bodyToString(request.body());

            JsonParser parser = new JsonParser();
            if (parser.parse(body).isJsonObject()) {
                try {
                    JSONObject json = new JSONObject(body);
                    judoShield.deviceSignal(json);

                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody requestBody = RequestBody.create(mediaType, json.toString());

                    return chain.proceed(request.newBuilder()
                            .post(requestBody)
                            .build());
                } catch (JSONException ignore) { }
            } else {
                return chain.proceed(request);
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
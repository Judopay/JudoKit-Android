package com.judopay.api;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.judopay.shield.JudoShield;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

class JudoShieldInterceptor implements Interceptor {

    private final JudoShield judoShield;
    private final String deviceId;

    JudoShieldInterceptor(Context context, String deviceId) {
        this.judoShield = new JudoShield(context);
        this.deviceId = deviceId;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        if (request.body() != null && "POST".equals(request.method())) {
            String body = bodyToString(request.body());

            JsonParser parser = new JsonParser();
            JsonElement jsonBody = parser.parse(body);

            if (jsonBody.isJsonObject()) {
                JsonObject json = jsonBody.getAsJsonObject();

                Map<String, String> signals = judoShield.deviceSignal(deviceId);
                JsonObject clientDetailsJson = new JsonObject();

                for (Map.Entry<String, String> entry : signals.entrySet()) {
                    clientDetailsJson.addProperty(entry.getKey(), entry.getValue());
                }

                json.add("clientDetails", clientDetailsJson);

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody requestBody = RequestBody.create(mediaType, json.toString());

                return chain.proceed(request.newBuilder()
                        .post(requestBody)
                        .build());
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
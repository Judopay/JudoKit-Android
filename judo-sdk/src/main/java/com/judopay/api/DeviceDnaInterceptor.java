package com.judopay.api;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.judopay.devicedna.DeviceDNA;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

class DeviceDnaInterceptor implements Interceptor {
    private static final String CLIENT_DETAILS = "clientDetails";
    private final DeviceDNA deviceDna;

    DeviceDnaInterceptor(final Context context) {
        this.deviceDna = new DeviceDNA(context);
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        if (isPost(request) && request.body() != null) {
            JsonElement body = getJsonRequestBody(request.body());

            if (body.isJsonObject()) {
                JsonObject json = body.getAsJsonObject();

                if (json.get(CLIENT_DETAILS) == null) {
                    addClientDetails(json);
                }

                return chain.proceed(request.newBuilder()
                        .post(getJsonRequestBody(json))
                        .build());
            }
        }
        return chain.proceed(request);
    }

    private void addClientDetails(final JsonObject json) {
        Map<String, String> signals = deviceDna.getDeviceDNA();
        JsonObject clientDetailsJson = new JsonObject();

        for (Map.Entry<String, String> entry : signals.entrySet()) {
            clientDetailsJson.addProperty(entry.getKey(), entry.getValue());
        }

        json.add(CLIENT_DETAILS, clientDetailsJson);
    }

    private boolean isPost(final okhttp3.Request request) {
        return request.body() != null && "POST".equals(request.method());
    }

    private RequestBody getJsonRequestBody(final JsonObject json) {
        MediaType mediaType = MediaType.parse("application/json");

        return RequestBody.create(mediaType, json.toString());
    }

    private JsonElement getJsonRequestBody(final RequestBody request) throws IOException {
        final Buffer buffer = new Buffer();

        request.writeTo(buffer);
        String body = buffer.readUtf8();

        JsonParser parser = new JsonParser();
        return parser.parse(body);
    }
}

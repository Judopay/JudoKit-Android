package com.judopay.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okio.Buffer;

public class DeDuplicationInterceptor implements Interceptor {

    private static final Map<String, Response> uniqueResponses = new HashMap<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (request.body() != null) {
            String body = bodyToString(request.body());

            JsonParser parser = new JsonParser();
            if (parser.parse(body).isJsonObject()) {
                JsonElement uniqueReference = parser.parse(body).getAsJsonObject().get("yourPaymentReference");

                if (uniqueReference != null && uniqueResponses.containsKey(uniqueReference.getAsString())) {
                    return uniqueResponses.get(uniqueReference.getAsString());
                } else {
                    Response response = chain.proceed(request);

                    if (uniqueReference != null) {
                        uniqueResponses.put(uniqueReference.getAsString(), response);
                    }
                    return response;
                }
            } else {
                return chain.proceed(request);
            }
        } else {
            return chain.proceed(request);
        }
    }

    private String bodyToString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "";
        }
    }

}
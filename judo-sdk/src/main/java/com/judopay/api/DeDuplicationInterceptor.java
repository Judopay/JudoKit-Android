package com.judopay.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okio.Buffer;

class DeDuplicationInterceptor implements Interceptor {

    private static final List<String> uniqueResponses = new ArrayList<>();

    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        okhttp3.Request request = chain.request();

        if (request.body() != null) {
            String body = bodyToString(request.body());

            JsonParser parser = new JsonParser();
            if (parser.parse(body).isJsonObject()) {
                JsonElement uniqueReference = parser.parse(body).getAsJsonObject().get("yourPaymentReference");

                if (uniqueReference != null && uniqueResponses.contains(uniqueReference.getAsString())) {
                    throw new DuplicationTransactionException(uniqueReference.getAsString());
                } else {
                    okhttp3.Response response = chain.proceed(request);

                    if (uniqueReference != null) {
                        uniqueResponses.add(uniqueReference.getAsString());
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

    private String bodyToString(final okhttp3.RequestBody request) throws IOException {
        final Buffer buffer = new Buffer();
        request.writeTo(buffer);
        return buffer.readUtf8();
    }

}
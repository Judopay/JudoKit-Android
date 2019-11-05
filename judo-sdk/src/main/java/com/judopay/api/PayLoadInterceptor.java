package com.judopay.api;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.judopay.arch.PayLoadUtil;
import com.judopay.model.EnhancedPaymentDetail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class PayLoadInterceptor implements Interceptor {

    private static final String ENHANCED_PAYMENT_DETAIL = "EnhancedPaymentDetail";
    private final List<String> payLoadPaths = Arrays.asList(
            "/transactions/payments",
            "/transactions/preauths",
            "/transactions/registercard",
            "/transactions/checkcard");

    private final Context context;

    PayLoadInterceptor(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request request = chain.request();
        final String path = request.url().encodedPath();

        if (payLoadPaths.contains(path) && request.body() != null) {

            final JsonElement jsonElement = convertRequestBodyToJson(request);
            if (jsonElement != null) {
                final JsonObject json = jsonElement.getAsJsonObject();
                if (json.get(ENHANCED_PAYMENT_DETAIL) == null) {
                    final JsonObject enhancedPaymentDetail = getEnhancedPaymentDetail();
                    json.add(ENHANCED_PAYMENT_DETAIL, enhancedPaymentDetail);
                }

                return chain.proceed(request.newBuilder()
                        .post(convertJsonToRequestBody(json))
                        .build());
            }
        }

        return chain.proceed(request);
    }

    private JsonObject getEnhancedPaymentDetail() {
        final EnhancedPaymentDetail enhancedPaymentDetail = PayLoadUtil.getEnhancedPaymentDetail(context);

        final Gson gson = new Gson();
        final String paymentDetail = gson.toJson(enhancedPaymentDetail);
        final JsonElement jsonElement = new JsonParser().parse(paymentDetail);

        return jsonElement.getAsJsonObject();
    }

    private RequestBody convertJsonToRequestBody(final JsonObject json) {
        final MediaType mediaType = MediaType.parse("application/json");

        return RequestBody.create(mediaType, json.toString());
    }

    private JsonElement convertRequestBodyToJson(final Request request) {
        final Buffer buffer = new Buffer();
        try {
            final Request copy = request.newBuilder().build();
            final RequestBody requestBody = copy.body();
            if (requestBody != null) {
                requestBody.writeTo(buffer);
                final String body = buffer.readUtf8();
                final JsonParser parser = new JsonParser();
                return parser.parse(body);
            }
        } catch (IOException ignore) {
        } finally {
            buffer.close();
        }
        return null;
    }
}

package com.judopay.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockAndroidPayInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        MediaType mediaType = MediaType.parse("application/json");

        String json = "{\"yourPaymentReference\": \"uniqueRef\", \"uniqueRequest\": true}";
        ResponseBody body = ResponseBody.create(mediaType, json);

        if (request.url().encodedPath().contains("androidpay")) {
            return new Response.Builder()
                    .request(request)
                    .code(200)
                    .body(body)
                    .protocol(Protocol.HTTP_1_1)
                    .build();
        } else {
            return chain.proceed(chain.request());
        }
    }

}

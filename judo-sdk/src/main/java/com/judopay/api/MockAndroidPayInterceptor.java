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

        String json = "{\n" +
                "  \"receiptId\": \"3021003\",\n" +
                "  \"yourPaymentReference\": \"a37b2722-6cbd-410c-af97-3652083340d1\",\n" +
                "  \"type\": \"Payment\",\n" +
                "  \"createdAt\": \"2016-02-16T14:50:08.3249+00:00\",\n" +
                "  \"result\": \"Success\",\n" +
                "  \"message\": \"AuthCode: 518485\",\n" +
                "  \"judoId\": 100407196,\n" +
                "  \"merchantName\": \"AndroidTESTapp\",\n" +
                "  \"appearsOnStatementAs\": \"AndroidTESTapp           \",\n" +
                "  \"originalAmount\": \"1.99\",\n" +
                "  \"netAmount\": \"1.99\",\n" +
                "  \"amount\": \"1.99\",\n" +
                "  \"currency\": \"GBP\",\n" +
                "  \"cardDetails\": {\n" +
                "    \"cardLastfour\": \"3436\",\n" +
                "    \"endDate\": \"1220\",\n" +
                "    \"cardToken\": \"f1BMzrYTBq7U9DDKYKFykVIM8u6AfiEg\",\n" +
                "    \"cardType\": 1\n" +
                "  },\n" +
                "  \"consumer\": {\n" +
                "    \"consumerToken\": \"c4jr6yNAU7ou3DBB\",\n" +
                "    \"yourConsumerReference\": \"consumerRef\"\n" +
                "  }\n" +
                "}";

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

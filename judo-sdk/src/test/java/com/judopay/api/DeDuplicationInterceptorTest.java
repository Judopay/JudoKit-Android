package com.judopay.api;

import com.judopay.exception.DuplicateTransactionError;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeDuplicationInterceptorTest {

    @Test(expected = DuplicateTransactionError.class)
    public void shouldThrowDuplicateTransactionExceptionWhenDuplicate() throws IOException {
        DeDuplicationInterceptor interceptor = new DeDuplicationInterceptor();

        MediaType mediaType = MediaType.parse("application/json");

        String json = "{\"yourPaymentReference\": \"uniqueRef\", \"uniqueRequest\": true}";
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url("http://www.judopay.com")
                .post(body)
                .build();

        ResponseBody responseBody = ResponseBody.create(mediaType, "");
        Response response = new Response.Builder()
                .request(request)
                .body(responseBody)
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();

        Interceptor.Chain chain = mock(Interceptor.Chain.class);

        when(chain.request()).thenReturn(request);

        when(chain.proceed(eq(request)))
                .thenReturn(response);

        interceptor.intercept(chain);
        interceptor.intercept(chain);
    }

    @Test
    public void shouldProcessWhenRequestBodyNotJson() throws IOException {
        DeDuplicationInterceptor interceptor = new DeDuplicationInterceptor();

        MediaType textHtmlMediaType = MediaType.parse("text/html");
        RequestBody body = RequestBody.create(textHtmlMediaType, "");
        Request request = new Request.Builder()
                .url("http://www.judopay.com")
                .post(body)
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        ResponseBody responseBody = ResponseBody.create(mediaType, "");
        Response response = new Response.Builder()
                .request(request)
                .body(responseBody)
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();

        Interceptor.Chain chain = mock(Interceptor.Chain.class);

        when(chain.request()).thenReturn(request);

        when(chain.proceed(eq(request)))
                .thenReturn(response);

        interceptor.intercept(chain);

        verify(chain, times(1)).proceed(eq(request));
    }

    @Test
    public void shouldProceedWhenRequestBodyNull() throws IOException {
        DeDuplicationInterceptor interceptor = new DeDuplicationInterceptor();

        Request request = new Request.Builder()
                .url("http://www.judopay.com")
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        ResponseBody responseBody = ResponseBody.create(mediaType, "");
        Response response = new Response.Builder()
                .request(request)
                .body(responseBody)
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();


        Interceptor.Chain chain = mock(Interceptor.Chain.class);

        when(chain.request()).thenReturn(request);

        when(chain.proceed(eq(request)))
                .thenReturn(response);

        interceptor.intercept(chain);

        verify(chain, times(1)).proceed(eq(request));
    }

}
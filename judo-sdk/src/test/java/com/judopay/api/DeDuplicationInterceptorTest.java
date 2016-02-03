package com.judopay.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeDuplicationInterceptorTest {

    @Mock
    Interceptor.Chain chain;

    @Test(expected = DuplicationTransactionException.class)
    public void shouldThrowDuplicateTransactionExceptionWhenDuplicate() throws IOException {
        DeDuplicationInterceptor interceptor = new DeDuplicationInterceptor();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{\"yourPaymentReference\": \"uniqueRef\"}");
        Request request = new Request.Builder()
                .url("http://www.judopay.com")
                .post(body)
                .build();

        Response response = new Response.Builder()
                .request(request)
                .body(ResponseBody.create(MediaType.parse("application/json"), ""))
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();

        when(chain.request()).thenReturn(request);

        when(chain.proceed(eq(request)))
                .thenReturn(response);

        interceptor.intercept(chain);
        interceptor.intercept(chain);
    }

    @Test
    public void shouldProcessWhenRequestBodyNotJson() throws IOException {
        DeDuplicationInterceptor interceptor = new DeDuplicationInterceptor();

        RequestBody body = RequestBody.create(MediaType.parse("text/html"), "");
        Request request = new Request.Builder()
                .url("http://www.judopay.com")
                .post(body)
                .build();

        Response response = new Response.Builder()
                .request(request)
                .body(ResponseBody.create(MediaType.parse("application/json"), ""))
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();

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

        Response response = new Response.Builder()
                .request(request)
                .body(ResponseBody.create(MediaType.parse("application/json"), ""))
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();

        when(chain.request()).thenReturn(request);

        when(chain.proceed(eq(request)))
                .thenReturn(response);

        interceptor.intercept(chain);

        verify(chain, times(1)).proceed(eq(request));
    }

}
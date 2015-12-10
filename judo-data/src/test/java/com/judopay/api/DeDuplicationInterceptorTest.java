package com.judopay.api;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeDuplicationInterceptorTest {

    @Mock
    Interceptor.Chain chain;

    @Test
    public void shouldReturnFirstRequestWhenDuplicatedRequest() throws IOException {
        DeDuplicationInterceptor interceptor = new DeDuplicationInterceptor();

        com.squareup.okhttp.Request request = new Request.Builder()
                .url("http://www.judopay.com")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "  \"yourPaymentReference\": \"1234567\"\n" +
                        "}"))
                .build();

        when(chain.request()).thenReturn(request);
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();

        when(chain.proceed(request)).thenReturn(response);
        Response firstResponse = interceptor.intercept(chain);
        assertThat(firstResponse, equalTo(response));

        Response secondResponse = interceptor.intercept(chain);
        assertThat(secondResponse, equalTo(firstResponse));
    }

    @Test
    public void shouldProceedIfNotJsonRequestBody() throws IOException {
        DeDuplicationInterceptor interceptor = new DeDuplicationInterceptor();

        com.squareup.okhttp.Request request = new Request.Builder()
                .url("http://www.judopay.com")
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .build();

        when(chain.request()).thenReturn(request);
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();

        when(chain.proceed(request)).thenReturn(response);

        Response okResponse = interceptor.intercept(chain);

        assertThat(okResponse, equalTo(response));
    }

}
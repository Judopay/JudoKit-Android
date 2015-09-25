package com.judopay.auth;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationInterceptorTest {

    @Test
    public void shouldAddAuthorizationHeader() throws IOException {
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor();

        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        Request.Builder requestBuilder = new Request.Builder()
                .url("http://www.judopay.com");

        Request request = requestBuilder.build();
        when(chain.request()).thenReturn(request);

        Response.Builder responseBuilder = new Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .request(request);

        when(chain.proceed(any(Request.class)))
                .thenReturn(responseBuilder.build());

        Response response = interceptor.intercept(chain);
        Headers headers = response.request().headers();

        assertThat(headers.get("Authorization"), notNullValue());
    }

}
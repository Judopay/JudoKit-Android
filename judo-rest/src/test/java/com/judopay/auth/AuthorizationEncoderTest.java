package com.judopay.auth;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationEncoderTest {

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenNoApiToken() {
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);

        when(context.getSharedPreferences(AuthorizationEncoder.SHARED_PREFS, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferences);

        when(sharedPreferences.getString("JudoApiToken", null))
                .thenReturn(null);

        AuthorizationEncoder authorizationEncoder = new AuthorizationEncoder(context);
        authorizationEncoder.getAuthorization();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenNoApiSecret() {
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);

        when(context.getSharedPreferences(AuthorizationEncoder.SHARED_PREFS, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferences);

        when(sharedPreferences.getString("JudoApiToken", null))
                .thenReturn("apiToken");

        when(sharedPreferences.getString("JudoApiSecret", null))
                .thenReturn(null);

        AuthorizationEncoder authorizationEncoder = new AuthorizationEncoder(context);
        authorizationEncoder.getAuthorization();
    }

}
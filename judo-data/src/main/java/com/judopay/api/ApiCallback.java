package com.judopay.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;

import retrofit.Callback;

public abstract class ApiCallback<T> implements Callback<T> {

    private final Class<T> responseClass;
    private final Gson gson;

    protected ApiCallback(Class<T> responseClass) {
        this.responseClass = responseClass;
        this.gson = JudoApiServiceFactory.getGsonBuilder().create();
    }

    public abstract void onResponse(T response);

    @Override
    public void onResponse(retrofit.Response<T> response) {
        if (response.isSuccess()) {
            onResponse(response.body());
        } else {
            try {
                Reader reader = response.errorBody().charStream();
                T errorResponse = gson.fromJson(reader, responseClass);
                onResponse(errorResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
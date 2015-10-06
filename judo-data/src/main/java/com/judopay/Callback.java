package com.judopay;

import retrofit.Response;

public abstract class Callback<T> implements retrofit.Callback<T> {

    public abstract void onSuccess(T t);

    public abstract void onFailure();

    @Override
    public void onResponse(Response<T> response) {
        if (response.isSuccess()) {
            onSuccess(response.body());
        } else {
            onFailure();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        onFailure();
    }

}
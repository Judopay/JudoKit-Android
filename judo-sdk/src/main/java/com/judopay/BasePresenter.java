package com.judopay;

import android.support.annotation.NonNull;

public abstract class BasePresenter<V extends BaseView> {

    private final V baseView;

    BasePresenter(@NonNull final V baseView) {
        this.baseView = baseView;
    }

    public V getView() {
        return baseView;
    }
}

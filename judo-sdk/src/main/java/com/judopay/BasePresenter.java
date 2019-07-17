package com.judopay;

public abstract class BasePresenter<V extends BaseView> {

    private final V baseView;

    BasePresenter(V baseView) {
        this.baseView = baseView;
    }

    public V getView() {
        return baseView;
    }
}

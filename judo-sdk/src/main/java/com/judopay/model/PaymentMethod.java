package com.judopay.model;

import androidx.annotation.IdRes;

import com.judopay.R;

public enum PaymentMethod {
    CREATE_PAYMENT(R.id.btnCardPayment),
    GPAY_PAYMENT(R.id.btnGPAY);

    private final int viewId;

    PaymentMethod(@IdRes final int viewId) {
        this.viewId = viewId;
    }

    public int getViewId() {
        return viewId;
    }
}

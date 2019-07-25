package com.judopay.model;

import android.support.annotation.IdRes;

import com.judopay.R;

public enum PaymentMethod {
    CREATE_PAYMENT(R.id.btnCardPayment),
    PBBA_PAYMENT(R.id.btnPBBA),
    GPAY_PAYMENT(R.id.btnGPAY);

    private final int viewId;

    PaymentMethod(@IdRes final int viewId) {
        this.viewId = viewId;
    }

    public int getViewId() {
        return viewId;
    }
}

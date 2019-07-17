package com.judopay;

import android.support.annotation.IdRes;

interface PaymentMethodView extends BaseView {
    void displayPaymentMethodView(@IdRes int viewId);

    void displayAllPaymentMethods();
}

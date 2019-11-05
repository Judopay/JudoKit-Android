package com.judopay;

import androidx.annotation.IdRes;

interface PaymentMethodView extends BaseView {
    void displayPaymentMethodView(@IdRes int viewId);

    void displayAllPaymentMethods();

    void setUpGPayButton();
}

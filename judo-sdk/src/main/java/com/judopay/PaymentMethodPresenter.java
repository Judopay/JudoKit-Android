package com.judopay;

import com.judopay.model.PaymentMethod;

import java.util.EnumSet;

class PaymentMethodPresenter extends BasePresenter<PaymentMethodView> {

    PaymentMethodPresenter(PaymentMethodView paymentMethodView) {
        super(paymentMethodView);
    }

    void setPaymentMethod(EnumSet<PaymentMethod> paymentMethodEnumSet) {

        if (paymentMethodEnumSet == null || paymentMethodEnumSet.isEmpty()) {
            getView().displayAllPaymentMethods();
        } else {
            for (PaymentMethod paymentMethod : paymentMethodEnumSet) {
                getView().displayPaymentMethodView(paymentMethod.getViewId());
            }
        }
    }
}

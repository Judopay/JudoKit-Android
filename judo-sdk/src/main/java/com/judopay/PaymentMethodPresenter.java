package com.judopay;

import com.judopay.model.PaymentMethod;

import java.util.EnumSet;

class PaymentMethodPresenter extends BasePresenter<PaymentMethodView> {

    PaymentMethodPresenter(final PaymentMethodView paymentMethodView) {
        super(paymentMethodView);
    }

    void setPaymentMethod(final EnumSet<PaymentMethod> paymentMethodEnumSet) {

        if (paymentMethodEnumSet == null || paymentMethodEnumSet.isEmpty()) {
            getView().displayAllPaymentMethods();
        } else {
            for (PaymentMethod paymentMethod : paymentMethodEnumSet) {
                if (paymentMethod == PaymentMethod.GPAY_PAYMENT) {
                    getView().setUpGPayButton();
                } else {
                    getView().displayPaymentMethodView(paymentMethod.getViewId());
                }
            }
        }
    }
}

package com.judopay.register;

import com.judopay.payment.Receipt;

interface PaymentFormView {

    void showLoading();

    void hideLoading();

    void finish(Receipt receipt);

    void showDeclinedMessage(Receipt receipt);

}

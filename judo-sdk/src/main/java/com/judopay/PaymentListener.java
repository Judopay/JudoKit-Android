package com.judopay;

import com.judopay.payment.Receipt;

interface PaymentListener {

    void onPaymentSuccess(Receipt receipt);

    void onPaymentDeclined(Receipt receipt);

    void onError();

}
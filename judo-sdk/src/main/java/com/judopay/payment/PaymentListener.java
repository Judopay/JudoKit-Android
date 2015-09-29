package com.judopay.payment;

import retrofit.Response;

public interface PaymentListener {

    void onPaymentSuccess(Response<PaymentResponse> response);

    void onFailure(Throwable t);

}

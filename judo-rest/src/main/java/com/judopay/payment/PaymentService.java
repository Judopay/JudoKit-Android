package com.judopay.payment;

import com.judopay.arch.api.RetrofitFactory;

import retrofit.Call;
import retrofit.Callback;

public class PaymentService {

    private PaymentApiService paymentApiService;

    public PaymentService(PaymentApiService paymentApiService) {
        this.paymentApiService = paymentApiService;
    }

    public PaymentService() {
        this.paymentApiService = RetrofitFactory.get()
                .create(PaymentApiService.class);
    }

    public void payment(Transaction transaction, Callback<PaymentResponse> callback) {
        Call<PaymentResponse> call = paymentApiService.payment(transaction);
        call.enqueue(callback);
    }

}

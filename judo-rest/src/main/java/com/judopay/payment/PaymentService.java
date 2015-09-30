package com.judopay.payment;

import com.judopay.arch.api.RetrofitFactory;

import retrofit.Call;
import retrofit.Callback;

public class PaymentService {

    private PaymentsApiService paymentsApiService;

    public PaymentService(PaymentsApiService paymentsApiService) {
        this.paymentsApiService = paymentsApiService;
    }

    public PaymentService() {
        this.paymentsApiService = RetrofitFactory.get().create(PaymentsApiService.class);
    }

    public void payment(Transaction transaction, Callback<PaymentResponse> callback) {
        Call<PaymentResponse> call = paymentsApiService.payment(transaction);
        call.enqueue(callback);
    }

}

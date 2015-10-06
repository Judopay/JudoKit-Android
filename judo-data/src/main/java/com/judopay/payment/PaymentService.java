package com.judopay.payment;

import com.judopay.arch.api.RetrofitFactory;

import rx.Observable;

public class PaymentService {

    private PaymentApiService paymentApiService;

    public PaymentService(PaymentApiService paymentApiService) {
        this.paymentApiService = paymentApiService;
    }

    public PaymentService() {
        this.paymentApiService = RetrofitFactory.get()
                .create(PaymentApiService.class);
    }

    public Observable<PaymentResponse> payment(Transaction transaction) {
        return paymentApiService.payment(transaction);
    }

}
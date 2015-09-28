package com.judopay.payment;

import com.judopay.JudoPay;
import com.judopay.auth.AuthenticationInterceptor;
import com.judopay.auth.AuthorizationEncoder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Retrofit;

public class PaymentService {

    private PaymentsApiService paymentsApiService;

    public PaymentService(PaymentsApiService paymentsApiService) {
        this.paymentsApiService = paymentsApiService;
    }

    public PaymentService() {
        AuthorizationEncoder authorizationEncoder = new AuthorizationEncoder(JudoPay.getContext());
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authorizationEncoder);

        OkHttpClient client = new OkHttpClient();
        client.interceptors()
                .add(interceptor);

        this.paymentsApiService = new Retrofit.Builder()
                .baseUrl(JudoPay.getBaseUrl())
                .client(client)
                .build()
                .create(PaymentsApiService.class);
    }

    public PaymentResponse payment(Transaction transaction) {
        return paymentsApiService.payment(transaction);
    }

}

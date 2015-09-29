package com.judopay.payment;

import com.google.gson.GsonBuilder;
import com.judopay.JudoPay;
import com.judopay.auth.AuthenticationInterceptor;
import com.judopay.auth.AuthorizationEncoder;
import com.judopay.util.DateJsonDeserializer;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

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

        GsonConverterFactory converterFactory = GsonConverterFactory.create(new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateJsonDeserializer())
                .create());

        this.paymentsApiService = new Retrofit.Builder()
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(JudoPay.getBaseUrl())
                .client(client)
                .build()
                .create(PaymentsApiService.class);
    }

    public void payment(Transaction transaction, Callback<PaymentResponse> callback) {
        Call<PaymentResponse> call = paymentsApiService.payment(transaction);
        call.enqueue(callback);
    }

}

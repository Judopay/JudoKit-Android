package com.judopay;

import android.os.Bundle;

import com.judopay.arch.api.RetrofitFactory;

public class PaymentFragment extends BasePaymentFragment {

    public static PaymentFragment newInstance(Payment payment) {
        PaymentFragment paymentFragment = new PaymentFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(JudoPay.EXTRA_PAYMENT, payment);
        paymentFragment.setArguments(arguments);

        return paymentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Payment payment = getArguments().getParcelable(JudoPay.EXTRA_PAYMENT);

        if (payment == null) {
            throw new RuntimeException("Payment extra must be provided to PaymentFragment");
        }

        if(savedInstanceState == null) {
            this.presenter = new PaymentPresenter(this, RetrofitFactory.getInstance().create(JudoApiService.class), new AndroidScheduler(), payment);
        }
    }

}
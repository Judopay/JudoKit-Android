package com.judopay;

import android.os.Bundle;

import com.judopay.arch.api.RetrofitFactory;

public class PreAuthFragment extends BasePaymentFragment {

    public static PreAuthFragment newInstance(Payment payment) {
        PreAuthFragment paymentFragment = new PreAuthFragment();

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
            throw new RuntimeException("Payment extra must be provided to PreAuthFragment");
        }

        if (savedInstanceState == null) {
            this.presenter = new PreAuthPresenter(this, RetrofitFactory.getInstance().create(JudoApiService.class), new AndroidScheduler(), payment);
        }
    }

}
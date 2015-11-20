package com.judopay;

import android.os.Bundle;

import com.judopay.arch.api.RetrofitFactory;

public class TokenPaymentFragment extends BasePaymentFragment {

    public static final String KEY_TOKEN_PAYMENT = "Judo-TokenPayment";

    public static TokenPaymentFragment newInstance(TokenPayment tokenPayment) {
        TokenPaymentFragment paymentFragment = new TokenPaymentFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_TOKEN_PAYMENT, tokenPayment);
        paymentFragment.setArguments(arguments);

        return paymentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TokenPayment tokenPayment = getArguments().getParcelable(KEY_TOKEN_PAYMENT);

        if (tokenPayment == null) {
            throw new RuntimeException("TokenPayment argument must be provided to TokenPaymentFragment");
        }

        if (savedInstanceState == null) {
            this.presenter = new TokenPaymentPresenter(this, RetrofitFactory.getInstance().create(JudoApiService.class), new AndroidScheduler(), tokenPayment);
        }
    }

}
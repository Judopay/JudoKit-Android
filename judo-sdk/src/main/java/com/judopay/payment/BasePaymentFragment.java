package com.judopay.payment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.JudoApiService;
import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.payment.form.PaymentFormFragment;

import rx.Observer;

public abstract class BasePaymentFragment extends Fragment implements PaymentFormListener, Observer<Receipt> {

    private static final String TAG_PAYMENT_FORM = "PaymentFormFragment";
    public static final String KEY_TOKEN_PAYMENT = "tokenPayment";

    protected View progressBar;
    protected PaymentListener paymentListener;
    protected JudoApiService judoApiService;

    private boolean paymentInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        this.judoApiService = RetrofitFactory.getInstance()
                .create(JudoApiService.class);
    }

    public void setPaymentListener(PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressBar = view.findViewById(R.id.progress_overlay);

        if (paymentInProgress) {
            showLoading();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        PaymentFormFragment paymentFormFragment = (PaymentFormFragment) fm.findFragmentByTag(TAG_PAYMENT_FORM);

        if (paymentFormFragment == null) {
            TokenPayment tokenPayment = getArguments().getParcelable(KEY_TOKEN_PAYMENT);

            if (tokenPayment != null) {
                paymentFormFragment = PaymentFormFragment.newInstance(tokenPayment.getCardToken(), this);
            } else {
                paymentFormFragment = PaymentFormFragment.newInstance(this);
            }

            paymentFormFragment.setTargetFragment(this, 0);

            fm.beginTransaction()
                    .add(R.id.container, paymentFormFragment, TAG_PAYMENT_FORM)
                    .commit();
        } else {
            paymentFormFragment.setPaymentFormListener(this);
        }
    }

    @Override
    public void onCompleted() {
        onLoadFinished();
    }

    @Override
    public void onError(Throwable e) {
        onLoadFinished();
    }

    @Override
    public void onNext(Receipt receipt) {
        if (receipt.isSuccess()) {
            paymentListener.onPaymentSuccess(receipt);
        } else {
            paymentListener.onPaymentDeclined(receipt);
        }
    }

    protected void onLoadFinished() {
        progressBar.setVisibility(View.GONE);
        paymentInProgress = false;
    }

    protected void onLoadStarted() {
        showLoading();
        paymentInProgress = true;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

}

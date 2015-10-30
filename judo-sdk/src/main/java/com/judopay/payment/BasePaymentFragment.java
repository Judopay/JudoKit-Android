package com.judopay.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.payment.form.PaymentFormFragment;

import rx.Observer;

import static com.judopay.JudoPay.EXTRA_PAYMENT;

public abstract class BasePaymentFragment extends Fragment implements PaymentFormListener, Observer<PaymentResponse> {

    private static final String TAG_PAYMENT_FORM = "PaymentFormFragment";

    protected View progressBar;
    protected PaymentListener paymentListener;
    protected PaymentApiService paymentApiService;

    private boolean paymentInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        this.paymentApiService = RetrofitFactory.getInstance()
                .create(PaymentApiService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressBar = view.findViewById(R.id.progress_container);

        if(paymentInProgress) {
            showLoading();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        PaymentFormFragment paymentFormFragment = (PaymentFormFragment) fm.findFragmentByTag(TAG_PAYMENT_FORM);

        if (paymentFormFragment == null) {
            paymentFormFragment = PaymentFormFragment.newInstance(getArguments().getParcelable(EXTRA_PAYMENT), this);
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
    public void onNext(PaymentResponse paymentResponse) {
        if (paymentResponse.isSuccess()) {
            paymentListener.onPaymentSuccess(paymentResponse);
        } else {
            paymentListener.onPaymentDeclined(paymentResponse);
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

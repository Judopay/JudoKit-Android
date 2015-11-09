package com.judopay.payment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.payment.form.PaymentFormFragment;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureListener;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BasePaymentFragment extends Fragment implements PaymentFormListener, ThreeDSecureListener, Observer<Receipt> {

    private static final String TAG_PAYMENT_FORM = "PaymentFormFragment";
    public static final String KEY_TOKEN_PAYMENT = "tokenPayment";

    protected static final String TAG_3DS_DIALOG = "3dSecureDialog";

    protected View progressBar;
    protected PaymentListener paymentListener;
    protected PaymentApiService paymentApiService;

    private boolean paymentInProgress;

    public static final String REDIRECT_URL = "http://pay.android-3ds-parser.testweb01.hq.judo/Android/Parse3DS";

    protected ThreeDSecureDialogFragment dialog;

    public boolean isPaymentInProgress() {
        return paymentInProgress;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        this.paymentApiService = RetrofitFactory.getInstance()
                .create(PaymentApiService.class);
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

        this.progressBar = view.findViewById(R.id.progress_container);

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
    public void onCompleted() { }

    @Override
    public void onError(Throwable e) { }

    @Override
    public void onNext(Receipt receipt) {
        if (receipt.isSuccess()) {
            onLoadFinished();
            paymentListener.onPaymentSuccess(receipt);
        } else {
            if (JudoPay.isThreeDSecureEnabled() && receipt.is3dSecureRequired()) {
                FragmentManager fm = getFragmentManager();
                dialog = new ThreeDSecureDialogFragment();
                dialog.setThreeDSecureListener(BasePaymentFragment.this);

                Bundle args = new Bundle();
                args.putString(ThreeDSecureDialogFragment.KEY_ACS_URL, receipt.getAcsUrl());
                args.putString(ThreeDSecureDialogFragment.KEY_MD, receipt.getMd());
                args.putString(ThreeDSecureDialogFragment.KEY_PA_REQ, receipt.getPaReq());
                args.putString(ThreeDSecureDialogFragment.KEY_TERM_URL, REDIRECT_URL);
                args.putString(ThreeDSecureDialogFragment.KEY_RECEIPT_ID, receipt.getReceiptId());
                dialog.setArguments(args);

                dialog.setCancelable(false);
                dialog.show(fm, TAG_3DS_DIALOG);

            } else {
                paymentListener.onPaymentDeclined(receipt);
            }
        }
    }

    @Override
    public void onAuthorizationCompleted(ThreeDSecureInfo threeDSecureInfo, String receiptId) {
        if (dialog != null && dialog.isVisible()) {
            dialog.dismiss();
        }

        paymentApiService.threeDSecurePayment(receiptId, threeDSecureInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl) { }

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

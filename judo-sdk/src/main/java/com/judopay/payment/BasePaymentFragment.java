package com.judopay.payment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.judopay.JudoApiService;
import com.judopay.JudoPay;
import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.payment.form.PaymentFormFragment;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureListener;
import com.judopay.secure3d.ThreeDSecureWebView;
import com.judopay.token.TokenPayment;

import java.io.IOException;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BasePaymentFragment extends Fragment implements PaymentFormListener, ThreeDSecureListener, Observer<Receipt> {

    private static final String TAG_PAYMENT_FORM = "PaymentFormFragment";
    public static final String KEY_TOKEN_PAYMENT = "tokenPayment";

    protected static final String TAG_3DS_DIALOG = "3dSecureDialog";

    protected View progressBar;
    protected TextView progressText;
    protected PaymentListener paymentListener;
    protected JudoApiService judoApiService;

    private boolean paymentInProgress;

    protected ThreeDSecureDialogFragment threeDSecureDialog;
    private ThreeDSecureWebView threeDSecureWebView;

    public boolean isPaymentInProgress() {
        return paymentInProgress;
    }

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
        this.progressText = (TextView) view.findViewById(R.id.progress_text);
        this.threeDSecureWebView = (ThreeDSecureWebView) view.findViewById(R.id.three_d_secure_web_view);

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
    public void onError(Throwable e) {
        paymentListener.onError();
    }

    @Override
    public void onNext(Receipt receipt) {
        if (receipt.isSuccess()) {
            paymentListener.onPaymentSuccess(receipt);
            onLoadFinished();
        } else {
            try {
                handle3dSecureOrDeclinedPayment(receipt);
            } catch (IOException e) {
                paymentListener.onError();
            }
        }
    }

    private void handle3dSecureOrDeclinedPayment(Receipt receipt) throws IOException {
        if (JudoPay.isThreeDSecureEnabled() && receipt.is3dSecureRequired()) {
            progressText.setText(R.string.redirecting);

            threeDSecureWebView.setThreeDSecureListener(this);
            threeDSecureWebView.authorize(receipt.getAcsUrl(), receipt.getMd(), receipt.getPaReq(), receipt.getReceiptId());
        } else {
            paymentListener.onPaymentDeclined(receipt);
        }
    }

    private void show3dSecureDialog(String loadingText) {
        FragmentManager fm = getFragmentManager();

        threeDSecureDialog = new ThreeDSecureDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ThreeDSecureDialogFragment.KEY_LOADING_TEXT, loadingText);

        threeDSecureDialog.setArguments(arguments);
        threeDSecureDialog.setCancelable(false);

        threeDSecureDialog.setWebView(threeDSecureWebView);
        threeDSecureDialog.show(fm, TAG_3DS_DIALOG);
    }

    @Override
    public void onAuthorizationCompleted(ThreeDSecureInfo threeDSecureInfo, String receiptId) {
        judoApiService.threeDSecurePayment(receiptId, threeDSecureInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }
    
    @Override
    public void onAuthorizationWebPageLoaded() {
        show3dSecureDialog(get3dSecureLoadingText());
    }

    @Override
    public void onAuthorizationWebPageLoadingError(int errorCode, String description, String failingUrl) { }

    protected String get3dSecureLoadingText() {
        return getString(R.string.verifying_payment);
    }

    protected void onLoadFinished() {
        if (threeDSecureDialog != null && threeDSecureDialog.isVisible()) {
            threeDSecureDialog.dismiss();
        }

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

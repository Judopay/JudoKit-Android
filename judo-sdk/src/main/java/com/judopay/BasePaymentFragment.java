package com.judopay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.judopay.model.CardToken;
import com.judopay.model.Receipt;
import com.judopay.payment.form.PaymentFormListener;
import com.judopay.payment.form.PaymentFormFragment;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureListener;
import com.judopay.secure3d.ThreeDSecureWebView;

import java.io.IOException;

abstract class BasePaymentFragment extends Fragment implements PaymentFormView, PaymentFormListener {

    private static final String TAG_PAYMENT_FORM = "PaymentFormFragment";
    private static final String TAG_3DS_DIALOG = "3dSecureDialog";

    protected View progressBar;
    protected TextView progressText;

    protected ThreeDSecureDialogFragment threeDSecureDialog;
    private ThreeDSecureWebView threeDSecureWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressBar = view.findViewById(R.id.progress_overlay);
        this.progressText = (TextView) view.findViewById(R.id.progress_text);
        this.threeDSecureWebView = (ThreeDSecureWebView) view.findViewById(R.id.three_d_secure_web_view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        PaymentFormFragment paymentFormFragment = (PaymentFormFragment) fm.findFragmentByTag(TAG_PAYMENT_FORM);

        if (paymentFormFragment == null) {
            CardToken cardToken = getArguments().getParcelable(JudoPay.JUDO_CARD_TOKEN);

            if (cardToken != null) {
                paymentFormFragment = PaymentFormFragment.newInstance(cardToken, this);
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
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void finish(Receipt receipt) {
        if (threeDSecureDialog != null && threeDSecureDialog.isVisible()) {
            threeDSecureDialog.dismiss();
        }

        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

        Activity activity = getActivity();

        if (activity != null) {
            activity.setResult(JudoPay.RESULT_REGISTER_CARD_SUCCESS, intent);
            activity.finish();
        }
    }

    @Override
    public void showDeclinedMessage(Receipt receipt) {
        if (receipt.isDeclined() && getArguments().getBoolean(JudoPay.JUDO_ALLOW_DECLINED_CARD_AMEND, true)) {
            Dialogs.createDeclinedPaymentDialog(getActivity()).show();
        } else {
            setDeclinedAndFinish(receipt);
        }
    }

    protected void setDeclinedAndFinish(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

        Activity activity = getActivity();

        if (activity != null) {
            activity.setResult(JudoPay.RESULT_REGISTER_CARD_DECLINED, intent);
            activity.finish();
        }
    }

    @Override
    public void setLoadingText(@StringRes int text) {
        this.progressText.setText(getString(text));
    }

    @Override
    public void start3dSecureWebView(Receipt receipt, ThreeDSecureListener listener) {
        threeDSecureWebView.setThreeDSecureListener(listener);
        try {
            threeDSecureWebView.authorize(receipt.getAcsUrl(), receipt.getMd(), receipt.getPaReq(), receipt.getReceiptId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show3dSecureWebView() {
        FragmentManager fm = getFragmentManager();

        threeDSecureDialog = new ThreeDSecureDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ThreeDSecureDialogFragment.KEY_LOADING_TEXT, getString(R.string.verifying_card));

        threeDSecureDialog.setArguments(arguments);
        threeDSecureDialog.setCancelable(false);

        threeDSecureDialog.setWebView(threeDSecureWebView);
        threeDSecureDialog.show(fm, TAG_3DS_DIALOG);
    }

    @Override
    public void handleError() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.setResult(JudoPay.RESULT_ERROR);
            activity.finish();
        }
    }

}

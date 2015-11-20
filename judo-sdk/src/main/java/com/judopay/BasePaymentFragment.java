package com.judopay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.judopay.payment.Receipt;
import com.judopay.payment.form.PaymentFormFragment;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureWebView;

import java.io.IOException;

abstract class BasePaymentFragment extends Fragment implements PaymentFormView {

    private static final String TAG_PAYMENT_FORM = "PaymentFormFragment";
    protected static final String TAG_3DS_DIALOG = "3dSecureDialog";

    protected View progressBar;
    protected TextView progressText;

    protected BasePaymentPresenter presenter;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressBar = view.findViewById(R.id.progress_overlay);
        this.progressText = (TextView) view.findViewById(R.id.progress_text);
        this.threeDSecureWebView = (ThreeDSecureWebView) view.findViewById(R.id.three_d_secure_web_view);

        this.presenter.reconnect();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        PaymentFormFragment paymentFormFragment = (PaymentFormFragment) fm.findFragmentByTag(TAG_PAYMENT_FORM);

        if (paymentFormFragment == null) {
            TokenPayment tokenPayment = getArguments().getParcelable(JudoPay.KEY_TOKEN_PAYMENT);

            if (tokenPayment != null) {
                paymentFormFragment = PaymentFormFragment.newInstance(tokenPayment.getCardToken(), presenter);
            } else {
                paymentFormFragment = PaymentFormFragment.newInstance(presenter);
            }

            paymentFormFragment.setTargetFragment(this, 0);

            fm.beginTransaction()
                    .add(R.id.container, paymentFormFragment, TAG_PAYMENT_FORM)
                    .commit();
        } else {
            paymentFormFragment.setPaymentFormListener(presenter);
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

        Activity activity = getActivity();

        if (activity != null) {
            Intent intent = new Intent();
            intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

            activity.setResult(JudoPay.RESULT_REGISTER_CARD_SUCCESS, intent);
            activity.finish();
        }
    }

    @Override
    public void showDeclinedMessage(Receipt receipt) {
        if (receipt.isDeclined() && getArguments().getBoolean(JudoPay.KEY_HANDLE_DECLINED)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.payment_failed)
                    .setMessage(R.string.please_check_details_try_again)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        } else {
            Intent intent = new Intent();
            intent.putExtra(JudoPay.JUDO_RECEIPT, receipt);

            Activity activity = getActivity();

            if (activity != null) {
                activity.setResult(JudoPay.RESULT_PAYMENT_DECLINED, intent);
                activity.finish();
            }
        }
    }

    @Override
    public void setLoadingText(@StringRes int text) {
        this.progressText.setText(getString(text));
    }

    @Override
    public void start3dSecureWebView(Receipt receipt) {
        threeDSecureWebView.setThreeDSecureListener(this.presenter);
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

}

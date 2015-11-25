package com.judopay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.judopay.arch.api.RetrofitFactory;
import com.judopay.customer.Card;
import com.judopay.payment.form.PaymentFormListener;
import com.judopay.payment.Receipt;
import com.judopay.payment.form.PaymentFormFragment;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureListener;
import com.judopay.secure3d.ThreeDSecureWebView;

import java.io.IOException;

import static com.judopay.JudoPay.JUDO_CONSUMER;

public class RegisterCardFragment extends Fragment implements PaymentFormView, PaymentFormListener {

    private static final String TAG_3DS_DIALOG = "3dSecureDialog";

    private RegisterCardPresenter presenter;
    private View progressOverlay;
    private TextView progressText;
    private ThreeDSecureWebView threeDSecureWebView;
    private ThreeDSecureDialogFragment threeDSecureDialog;
    private Consumer consumer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (savedInstanceState == null) {
            this.presenter = new RegisterCardPresenter(this, RetrofitFactory.getApiService(), new AndroidScheduler());

            PaymentFormFragment paymentFormFragment = PaymentFormFragment.newInstance(this, getString(R.string.add_card));
            paymentFormFragment.setRetainInstance(true);

            consumer = getArguments().getParcelable(JUDO_CONSUMER);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, paymentFormFragment)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_card, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressOverlay = view.findViewById(R.id.progress_overlay);
        this.progressText = (TextView) view.findViewById(R.id.progress_text);
        this.threeDSecureWebView = (ThreeDSecureWebView) view.findViewById(R.id.three_d_secure_web_view);

        this.presenter.reconnect();
    }

    @Override
    public void showLoading() {
        progressOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressOverlay.setVisibility(View.GONE);
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
    public void handleError() {
        Activity activity = getActivity();

        if (activity != null) {
            activity.setResult(JudoPay.RESULT_ERROR);
            activity.finish();
        }
    }

    @Override
    public void showDeclinedMessage(Receipt receipt) {
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
    public void start3dSecureWebView(Receipt receipt, ThreeDSecureListener threeDSecureListener) {
        threeDSecureWebView.setThreeDSecureListener(threeDSecureListener);
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
    public void onSubmit(Card card) {
        presenter.performRegisterCard(card, consumer, JudoPay.isThreeDSecureEnabled());
    }
}
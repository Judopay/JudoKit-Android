package com.judopay.register;

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

import com.judopay.AndroidScheduler;
import com.judopay.Consumer;
import com.judopay.JudoApiService;
import com.judopay.JudoPay;
import com.judopay.PaymentFormView;
import com.judopay.R;
import com.judopay.arch.api.RetrofitFactory;
import com.judopay.payment.Receipt;
import com.judopay.payment.form.PaymentFormFragment;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureWebView;

import java.io.IOException;

public class RegisterCardFragment extends Fragment implements PaymentFormView {

    public static final String KEY_CONSUMER = "Judo-Consumer";
    private static final String TAG_3DS_DIALOG = "3dSecureDialog";

    private RegisterCardPresenter presenter;
    private View progressOverlay;
    private TextView progressText;
    private ThreeDSecureWebView threeDSecureWebView;
    private ThreeDSecureDialogFragment threeDSecureDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (savedInstanceState == null) {
            Consumer consumer = getArguments().getParcelable(KEY_CONSUMER);

            this.presenter = new RegisterCardPresenter(this,
                    RetrofitFactory.getInstance(getActivity()).create(JudoApiService.class),
                    consumer,
                    new AndroidScheduler());

            PaymentFormFragment paymentFormFragment = PaymentFormFragment.newInstance(this.presenter, getString(R.string.add_card));
            paymentFormFragment.setRetainInstance(true);

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
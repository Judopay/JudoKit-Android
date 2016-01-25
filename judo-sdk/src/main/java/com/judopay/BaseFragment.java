package com.judopay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.judopay.model.CardToken;
import com.judopay.model.Receipt;
import com.judopay.card.CardEntryFragment;
import com.judopay.card.CardEntryListener;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureListener;
import com.judopay.secure3d.ThreeDSecureWebView;
import com.judopay.view.Dialogs;

abstract class BaseFragment extends Fragment implements PaymentFormView, CardEntryListener {

    private static final String TAG_PAYMENT_FORM = "CardEntryFragment";
    private static final String TAG_3DS_DIALOG = "3dSecureDialog";

    private View progressBar;
    private TextView progressText;

    private ThreeDSecureDialogFragment threeDSecureDialog;
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

        CardEntryFragment cardEntryFragment = (CardEntryFragment) getFragmentManager().findFragmentByTag(TAG_PAYMENT_FORM);

        if (cardEntryFragment == null) {
            cardEntryFragment = createPaymentFormFragment();
            cardEntryFragment.setTargetFragment(this, 0);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, cardEntryFragment, TAG_PAYMENT_FORM)
                    .commit();
        } else {
            cardEntryFragment.setCardEntryListener(this);
        }
    }

    CardEntryFragment createPaymentFormFragment() {
        JudoOptions judoOptions;

        if (getArguments().containsKey(Judo.JUDO_OPTIONS)) {
            judoOptions = getArguments().getParcelable(Judo.JUDO_OPTIONS);
        } else {
            CardToken cardToken = getArguments().getParcelable(Judo.JUDO_CARD_TOKEN);

            judoOptions = new JudoOptions.Builder()
                    .setCardToken(cardToken)
                    .build();
        }

        return CardEntryFragment.newInstance(judoOptions, this);
    }

    JudoOptions getJudoOptions() {
        Bundle args = getArguments();

        if (args.containsKey(Judo.JUDO_OPTIONS)) {
            return args.getParcelable(Judo.JUDO_OPTIONS);
        } else {
            return new JudoOptions.Builder()
                    .setJudoId(args.getString(Judo.JUDO_ID))
                    .setAmount(args.getString(Judo.JUDO_AMOUNT))
                    .setCardToken((CardToken) args.getParcelable(Judo.JUDO_CARD_TOKEN))
                    .setCurrency(args.getString(Judo.JUDO_CURRENCY))
                    .setConsumerRef(args.getString(Judo.JUDO_CONSUMER))
                    .setMetaData(args.getBundle(Judo.JUDO_META_DATA))
                    .build();
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
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        Activity activity = getActivity();

        if (activity != null) {
            activity.setResult(Judo.RESULT_SUCCESS, intent);
            activity.finish();
        }
    }

    @Override
    public void dismiss3dSecureDialog() {
        if (threeDSecureDialog != null && threeDSecureDialog.isVisible()) {
            threeDSecureDialog.dismiss();
            threeDSecureDialog = null;
        }
    }

    @Override
    public void showDeclinedMessage(Receipt receipt) {
        if (getArguments().getBoolean(Judo.JUDO_ALLOW_DECLINED_CARD_AMEND, true)) {
            Dialogs.createDeclinedPaymentDialog(getActivity()).show();
        } else {
            setDeclinedAndFinish(receipt);
        }
    }

    private void setDeclinedAndFinish(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        Activity activity = getActivity();

        if (activity != null) {
            activity.setResult(Judo.RESULT_DECLINED, intent);
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

        threeDSecureWebView.authorize(receipt.getAcsUrl(), receipt.getMd(), receipt.getPaReq(), receipt.getReceiptId());
    }

    @Override
    public void show3dSecureWebView() {
        if (threeDSecureDialog == null) {
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

    @Override
    public void handleError(@Nullable Receipt receipt) {
        Activity activity = getActivity();
        if (activity != null) {
            if (receipt != null) {
                Intent data = new Intent();
                data.putExtra(Judo.JUDO_RECEIPT, receipt);
                activity.setResult(Judo.RESULT_ERROR, data);
            } else {
                activity.setResult(Judo.RESULT_ERROR);
            }
            activity.finish();
        }
    }

    @Override
    public void showConnectionErrorDialog() {
        Dialogs.createConnectionErrorDialog(getActivity()).show();
    }
}

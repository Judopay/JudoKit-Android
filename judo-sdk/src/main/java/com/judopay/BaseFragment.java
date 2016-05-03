package com.judopay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.judopay.card.AbstractCardEntryFragment;
import com.judopay.card.CardEntryFragment;
import com.judopay.card.CardEntryListener;
import com.judopay.card.CustomLayoutCardEntryFragment;
import com.judopay.model.Receipt;
import com.judopay.secure3d.ThreeDSecureDialogFragment;
import com.judopay.secure3d.ThreeDSecureListener;
import com.judopay.secure3d.ThreeDSecureWebView;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static com.judopay.Judo.JUDO_OPTIONS;

abstract class BaseFragment extends Fragment implements TransactionCallbacks, CardEntryListener {

    private static final String TAG_PAYMENT_FORM = "CardEntryFragment";
    private static final String TAG_3DS_DIALOG = "3dSecureDialog";

    private View progressBar;
    private TextView progressText;

    private ThreeDSecureDialogFragment threeDSecureDialog;
    private ThreeDSecureWebView threeDSecureWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(JUDO_OPTIONS)) {
            throw new IllegalArgumentException(String.format("%s argument is required for %s", JUDO_OPTIONS, this.getClass().getSimpleName()));
        }

        setRetainInstance(true);
    }

    protected void checkJudoOptionsExtras(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new IllegalArgumentException("JudoOptions must contain all required fields");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.progressBar = view.findViewById(R.id.progress_overlay);
        this.progressText = (TextView) view.findViewById(R.id.progress_text);
        this.threeDSecureWebView = (ThreeDSecureWebView) view.findViewById(R.id.three_d_secure_web_view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AbstractCardEntryFragment cardEntryFragment = (AbstractCardEntryFragment) getFragmentManager().findFragmentByTag(TAG_PAYMENT_FORM);

        if (cardEntryFragment == null) {
            cardEntryFragment = createCardEntryFragment();
            cardEntryFragment.setTargetFragment(this, 0);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, cardEntryFragment, TAG_PAYMENT_FORM)
                    .commit();
        } else {
            cardEntryFragment.setCardEntryListener(this);
        }
    }

    @Override
    public void onSuccess(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        sendResult(Judo.RESULT_SUCCESS, intent);
    }

    @Override
    public void onDeclined(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        sendResult(Judo.RESULT_DECLINED, intent);
    }

    @Override
    public void onError(Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        sendResult(Judo.RESULT_ERROR, intent);
    }

    @Override
    public void onConnectionError() {
        sendResult(Judo.RESULT_CONNECTION_ERROR, new Intent());
    }

    private void sendResult(int resultCode, Intent intent) {
        Activity activity = getActivity();

        if(activity != null && !activity.isFinishing()) {
            try {
                PendingIntent pendingResult = activity.createPendingResult(Judo.JUDO_REQUEST, intent, FLAG_ONE_SHOT);
                pendingResult.send(resultCode);
            } catch (PendingIntent.CanceledException ignore) { }
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
    public void dismiss3dSecureDialog() {
        if (threeDSecureDialog != null && threeDSecureDialog.isVisible()) {
            threeDSecureDialog.dismiss();
            threeDSecureDialog = null;
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

    AbstractCardEntryFragment createCardEntryFragment() {
        JudoOptions options = getArguments().getParcelable(Judo.JUDO_OPTIONS);

        if(options != null && options.getCustomLayout() != null) {
            options.getCustomLayout().validate(getActivity());
            return CustomLayoutCardEntryFragment.newInstance(options, this);
        }

        return CardEntryFragment.newInstance(options, this);
    }

    JudoOptions getJudoOptions() {
        Bundle args = getArguments();
        return args.getParcelable(Judo.JUDO_OPTIONS);
    }

}
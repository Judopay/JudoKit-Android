package com.judopay;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.judopay.arch.ThemeUtil;
import com.judopay.card.AbstractCardEntryFragment;
import com.judopay.card.CardEntryFragment;
import com.judopay.card.CardEntryListener;
import com.judopay.card.CustomLayoutCardEntryFragment;
import com.judopay.card.TokenCardEntryFragment;
import com.judopay.cardverification.AuthorizationListener;
import com.judopay.cardverification.CardholderVerificationDialogFragment;
import com.judopay.model.Card;
import com.judopay.model.Receipt;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

abstract class JudoFragment extends BaseFragment implements TransactionCallbacks, CardEntryListener {
    private static final String TAG_3DS_DIALOG = "3dSecureDialog";

    private View progressBar;
    private TextView progressText;
    private ProgressListener listener;
    private CardholderVerificationDialogFragment cardholderVerificationDialogFragment;
    private AbstractCardEntryFragment cardEntryFragment;

    abstract boolean isTransactionInProgress();

    public void setProgressListener(final ProgressListener progressListener) {
        listener = progressListener;
    }

    private void notifyListener() {
        if (listener == null) {
            return;
        }
        if (isTransactionInProgress()) {
            listener.onProgressShown();
        } else {
            listener.onProgressDismissed();
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressText = view.findViewById(R.id.progress_text);
        progressBar = view.findViewById(R.id.progress_overlay);
        progressBar.setBackgroundColor(ThemeUtil.getColorAttr(getActivity(), R.attr.overlayBackground));
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (cardEntryFragment == null) {
            cardEntryFragment = createCardEntryFragment();
            cardEntryFragment.setTargetFragment(this, 0);

            if (getFragmentManager() != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, cardEntryFragment)
                        .commit();
            }
        } else {
            cardEntryFragment.setCardEntryListener(this);
        }
    }

    @Override
    public void onSuccess(final Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        sendResult(Judo.RESULT_SUCCESS, intent);
        notifyListener();
    }

    @Override
    public void onDeclined(final Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        sendResult(Judo.RESULT_DECLINED, intent);
        notifyListener();
    }

    @Override
    public void onError(final Receipt receipt) {
        Intent intent = new Intent();
        intent.putExtra(Judo.JUDO_RECEIPT, receipt);

        sendResult(Judo.RESULT_ERROR, intent);
        notifyListener();
    }

    @Override
    public void onConnectionError() {
        sendResult(Judo.RESULT_CONNECTION_ERROR, new Intent());
        notifyListener();
    }

    private void sendResult(final int resultCode, final Intent intent) {
        Activity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            try {
                PendingIntent pendingResult = activity.createPendingResult(Judo.JUDO_REQUEST, intent, FLAG_ONE_SHOT);
                pendingResult.send(resultCode);
            } catch (PendingIntent.CanceledException ignore) {
            }
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        notifyListener();
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        notifyListener();
    }

    @Override
    public void dismiss3dSecureDialog() {
        if (cardholderVerificationDialogFragment != null && cardholderVerificationDialogFragment.isVisible()) {
            cardholderVerificationDialogFragment.dismiss();
            cardholderVerificationDialogFragment = null;
        }
    }

    @Override
    public void setLoadingText(@StringRes final int text) {
        this.progressText.setText(getString(text));
    }

    @Override
    public void start3dSecureWebView(final Receipt receipt, final AuthorizationListener listener) {
        if (cardholderVerificationDialogFragment == null && getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();

            cardholderVerificationDialogFragment = new CardholderVerificationDialogFragment();

            Bundle arguments = new Bundle();
            arguments.putString(CardholderVerificationDialogFragment.KEY_LOADING_TEXT, getString(R.string.verifying_card));
            arguments.putParcelable(Judo.JUDO_RECEIPT, receipt);

            cardholderVerificationDialogFragment.setListener(listener);
            cardholderVerificationDialogFragment.setArguments(arguments);
            cardholderVerificationDialogFragment.show(manager, TAG_3DS_DIALOG);
        }
    }

    AbstractCardEntryFragment createCardEntryFragment() {
        Judo judo = getJudo();

        if (judo != null) {
            if (judo.getCustomLayout() != null) {
                judo.getCustomLayout().validate(getActivity());
                return CustomLayoutCardEntryFragment.newInstance(judo, this);
            } else if (judo.getCardToken() != null) {
                return TokenCardEntryFragment.newInstance(judo, this);
            }
        }
        return CardEntryFragment.newInstance(judo, this);
    }

    public void setCard(final Card card) {
        if (cardEntryFragment != null && card != null) {
            cardEntryFragment.setCard(card);
        }
    }
}

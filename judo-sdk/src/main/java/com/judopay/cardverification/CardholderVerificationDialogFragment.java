package com.judopay.cardverification;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.judopay.Judo;
import com.judopay.R;
import com.judopay.model.Receipt;


/**
 * A dialog for showing the web page during 3D-Secure verification.
 */
public class CardholderVerificationDialogFragment extends DialogFragment implements WebViewListener {

    public static final String KEY_LOADING_TEXT = "Judo-LoadingText";

    private View loadingView;
    private AuthorizationListener listener;
    private TextView loadingText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);
        return inflater.inflate(R.layout.dialog_card_verification, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.authentication);

        return dialog;
    }

    public void setListener(AuthorizationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingView = view.findViewById(R.id.card_verification_loading_overlay);

        CardVerificationWebView webView = (CardVerificationWebView) view.findViewById(R.id.card_verification_web_view);
        webView.setResultPageListener(this);

        loadingText = (TextView) view.findViewById(R.id.card_verification_loading_text);

        Bundle arguments = getArguments();
        Receipt receipt = arguments.getParcelable(Judo.JUDO_RECEIPT);

        if (receipt != null) {
            webView.setAuthorizationListener(listener);
            webView.authorize(receipt.getAcsUrl(), receipt.getMd(), receipt.getPaReq(), receipt.getReceiptId());
        }
    }

    @Override
    public void onResume() {
        resizeDialog();
        super.onResume();
    }

    private void resizeDialog() {
        LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
    }

    @Override
    public void onPageStarted() {
        if (loadingView != null) {
            Bundle arguments = getArguments();

            if (arguments != null && arguments.containsKey(KEY_LOADING_TEXT)) {
                loadingText.setText(arguments.getString(KEY_LOADING_TEXT));
            }

            loadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageLoaded() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

}
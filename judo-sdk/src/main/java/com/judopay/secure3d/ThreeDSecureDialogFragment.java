package com.judopay.secure3d;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.judopay.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * A dialog for showing the web page during 3D-Secure verification.
 */
public class ThreeDSecureDialogFragment extends DialogFragment implements ThreeDSecureResultPageListener {

    public static final String KEY_LOADING_TEXT = "Judo-LoadingText";

    private WebView webView;
    private View loadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_three_d_secure, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.authentication);

        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup webViewContainer = (ViewGroup) view.findViewById(R.id.web_view_container);
        loadingView = view.findViewById(R.id.loading_overlay_3dsecure);

        TextView loadingText = (TextView) view.findViewById(R.id.three_d_secure_progress_text);

        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey(KEY_LOADING_TEXT)) {
            loadingText.setText(arguments.getString(KEY_LOADING_TEXT));
        }

        webViewContainer.addView(webView);
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

    public void setWebView(ThreeDSecureWebView webView) {
        ViewGroup parent = (ViewGroup) webView.getParent();

        if (parent != null) {
            parent.removeView(webView);
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        webView.setId(R.id.three_d_secure_web_view);
        webView.setLayoutParams(params);
        webView.setVisibility(View.VISIBLE);

        webView.setResultPageListener(this);

        this.webView = webView;
    }

    @Override
    public void onPageStarted() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

}
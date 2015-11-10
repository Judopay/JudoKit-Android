package com.judopay.secure3d;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.judopay.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ThreeDSecureDialogFragment extends DialogFragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_three_d_secure, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup webViewContainer = (ViewGroup) view.findViewById(R.id.web_view_container);
        webViewContainer.addView(webView);
    }

    @Override
    public void onResume() {
        LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);

        super.onResume();
    }

    public void setWebView(WebView webView) {
        ViewGroup parent = (ViewGroup) webView.getParent();

        if (parent != null) {
            parent.removeView(webView);
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        webView.setLayoutParams(params);

        this.webView = webView;
    }

}
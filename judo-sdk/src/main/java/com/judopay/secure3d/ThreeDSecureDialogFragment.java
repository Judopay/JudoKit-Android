package com.judopay.secure3d;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judopay.R;

import java.io.IOException;

public class ThreeDSecureDialogFragment extends DialogFragment {

    public static final String KEY_TERM_URL = "Judo-TermUrl";
    public static final String KEY_ACS_URL = "Judo-AcsUrl";
    public static final String KEY_MD = "Judo-MD";
    public static final String KEY_PA_REQ = "Judo-PaReq";
    public static final String KEY_RECEIPT_ID = "Judo-ReceiptId";

    private ThreeDSecureListener threeDSecureListener;
    private ThreeDSecureWebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_three_d_secure, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = (ThreeDSecureWebView) view.findViewById(R.id.three_d_secure_web_view);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.setThreeDSecureListener(threeDSecureListener);
            load3dSecureAcsUrl();
        }
    }

    private void load3dSecureAcsUrl() {
        try {
            Bundle arguments = getArguments();
            String acsUrl = arguments.getString(KEY_ACS_URL);
            String md = arguments.getString(KEY_MD);
            String paReq = arguments.getString(KEY_PA_REQ);
            String termUrl = arguments.getString(KEY_TERM_URL);
            String receiptId = arguments.getString(KEY_RECEIPT_ID);

            webView.authorize(acsUrl, md, paReq, termUrl, receiptId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    public void setThreeDSecureListener(ThreeDSecureListener threeDSecureListener) {
        this.threeDSecureListener = threeDSecureListener;
    }

}
package com.judopay;

import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.judopay.model.SaleStatusRequest;

public class IdealWebViewClient extends WebViewClient {
    private IdealWebViewCallback callback;
    private String merchantRedirectUrl;

    IdealWebViewClient(final IdealWebViewCallback callback, String merchantRedirectUrl) {
        this.callback = callback;
        this.merchantRedirectUrl = merchantRedirectUrl;
    }

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if(url.contains(merchantRedirectUrl)){
            String checksum = url.split("cs=")[1];
            callback.onPageStarted(checksum);
        }
    }
}

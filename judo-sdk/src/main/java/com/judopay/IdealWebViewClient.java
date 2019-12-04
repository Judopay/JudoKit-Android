package com.judopay;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;

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
        try {
            String host = new URL(url).getHost();
            String merchantRedirectHost = new URL(merchantRedirectUrl).getHost();
            if (host.contains(merchantRedirectHost)) {
                String checksum = url.split("cs=")[1];
                callback.onPageStarted(checksum);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

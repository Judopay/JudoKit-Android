package com.judopay.secure3d;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.view.View.INVISIBLE;

class ThreeDSecureWebViewClient extends WebViewClient {

    private final String redirectUrl;
    private final String javaScriptNamespace;
    private final ThreeDSecureListener threeDSecureListener;

    private ThreeDSecureResultPageListener resultPageListener;

    public ThreeDSecureWebViewClient(String javaScriptNamespace, ThreeDSecureListener threeDSecureListener) {
        this.redirectUrl = ThreeDSecureWebView.REDIRECT_URL;
        this.javaScriptNamespace = javaScriptNamespace;
        this.threeDSecureListener = threeDSecureListener;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (url.equals(redirectUrl)) {
            view.loadUrl(String.format("javascript:window.%s.parseJsonFromHtml(document.documentElement.innerHTML);", javaScriptNamespace));
        } else {
            threeDSecureListener.onAuthorizationWebPageLoaded();
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (url.equals(redirectUrl)) {
            if (resultPageListener != null) {
                resultPageListener.onPageStarted();
            }
            view.setVisibility(INVISIBLE);
        }
    }

    public void setResultPageListener(ThreeDSecureResultPageListener resultPageListener) {
        this.resultPageListener = resultPageListener;
    }

}
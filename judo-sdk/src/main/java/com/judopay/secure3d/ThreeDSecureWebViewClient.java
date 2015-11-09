package com.judopay.secure3d;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ThreeDSecureWebViewClient extends WebViewClient {

    private final String postbackUrl;
    private final String javaScriptNamespace;
    private final ThreeDSecureListener threeDSecureListener;

    public ThreeDSecureWebViewClient(String postbackUrl, String javaScriptNamespace, ThreeDSecureListener threeDSecureListener) {
        this.postbackUrl = postbackUrl;
        this.javaScriptNamespace = javaScriptNamespace;
        this.threeDSecureListener = threeDSecureListener;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        view.zoomOut();

        if (url.equals(postbackUrl)) {
            view.loadUrl(String.format("javascript:window.%s.parseJsonFromHtml(document.getElementsByTagName('html')[0].innerHTML);", javaScriptNamespace));
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (url.equals(postbackUrl)) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        // log the error or something
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);

        if (!failingUrl.startsWith(postbackUrl)) {
            threeDSecureListener.onAuthorizationWebPageLoadingError(errorCode, description, failingUrl);
        }
    }

}
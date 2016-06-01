package com.judopay.cardverification;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.view.View.INVISIBLE;

class JsonRedirectWebViewClient extends WebViewClient {

    private final String javaScriptNamespace;

    private WebViewListener webViewListener;
    private final String redirectUrl;

    public JsonRedirectWebViewClient(String javaScriptNamespace, String redirectUrl) {
        this.javaScriptNamespace = javaScriptNamespace;
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (url.equals(redirectUrl)) {
            view.loadUrl(String.format("javascript:window.%s.parseJsonFromHtml(document.documentElement.innerHTML);", javaScriptNamespace));
        } else {
            webViewListener.onPageLoaded();
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (url.equals(CardVerificationWebView.REDIRECT_URL)) {
            if (webViewListener != null) {
                webViewListener.onPageStarted();
            }
            view.setVisibility(INVISIBLE);
        }
    }

    public void setWebViewListener(WebViewListener webViewListener) {
        this.webViewListener = webViewListener;
    }

}
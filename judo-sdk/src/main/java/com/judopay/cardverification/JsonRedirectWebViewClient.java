package com.judopay.cardverification;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.view.View.INVISIBLE;

class JsonRedirectWebViewClient extends WebViewClient {

    private final String javaScriptNamespace;

    private WebViewListener webViewListener;
    private final String redirectUrl;

    public JsonRedirectWebViewClient(final String javaScriptNamespace, final String redirectUrl) {
        this.javaScriptNamespace = javaScriptNamespace;
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void onPageFinished(final WebView view, final String url) {
        super.onPageFinished(view, url);

        if (url.equals(redirectUrl)) {
            view.loadUrl(String.format("javascript:window.%s.parseJsonFromHtml(document.documentElement.innerHTML);", javaScriptNamespace));
        } else {
            webViewListener.onPageLoaded();
        }
    }

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (url.equals(redirectUrl)) {
            if (webViewListener != null) {
                webViewListener.onPageStarted();
            }
            view.setVisibility(INVISIBLE);
        }
    }

    public void setWebViewListener(final WebViewListener webViewListener) {
        this.webViewListener = webViewListener;
    }

}
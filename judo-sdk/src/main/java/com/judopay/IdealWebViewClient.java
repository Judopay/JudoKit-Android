package com.judopay;

import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.judopay.model.SaleStatusRequest;

public class IdealWebViewClient extends WebViewClient {
    private IdealWebViewCallback callback;

    IdealWebViewClient(final IdealWebViewCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if(url.contains("txid=")){
            String checksum = url.split("cs=")[1];
            SaleStatusRequest saleStatusRequest = new SaleStatusRequest();
            saleStatusRequest.setChecksum(checksum);
            callback.onPageStarted(saleStatusRequest);
        }
    }
}

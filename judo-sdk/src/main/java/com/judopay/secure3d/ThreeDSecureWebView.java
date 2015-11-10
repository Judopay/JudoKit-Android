package com.judopay.secure3d;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.judopay.BuildConfig;
import com.judopay.payment.ThreeDSecureInfo;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ThreeDSecureWebView extends WebView implements JsonParsingJavaScriptInterface.JsonListener {

    private static final String JS_NAMESPACE = "JudoPay";

    private String postbackUrl;
    private ThreeDSecureListener threeDSecureListener;
    private String receiptId;

    private ThreeDSecureWebViewClient webViewClient;

    public ThreeDSecureWebView(Context context) {
        super(context);
        initialise();
    }

    public ThreeDSecureWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public ThreeDSecureWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ThreeDSecureWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initialise() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBuiltInZoomControls(true);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }

        addJavascriptInterface(new JsonParsingJavaScriptInterface(this), JS_NAMESPACE);
    }

    public void authorize(final String acsUrl, final String md, final String paReq, final String termUrl, String receiptId) throws IOException {
        List<NameValuePair> params = new LinkedList<>();

        params.add(new BasicNameValuePair("MD", md));
        params.add(new BasicNameValuePair("TermUrl", termUrl));
        params.add(new BasicNameValuePair("PaReq", paReq));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        new UrlEncodedFormEntity(params, HTTP.UTF_8).writeTo(bos);

        this.postbackUrl = termUrl;
        this.receiptId = receiptId;

        this.webViewClient = new ThreeDSecureWebViewClient(acsUrl, postbackUrl, JS_NAMESPACE, threeDSecureListener);
        setWebViewClient(webViewClient);

        postUrl(acsUrl, bos.toByteArray());
    }

    public void setThreeDSecureListener(ThreeDSecureListener threeDSecureListener) {
        this.threeDSecureListener = threeDSecureListener;
    }

    @Override
    public void onJsonReceived(String json) {
        Gson gson = new Gson();

        ThreeDSecureInfo threeDSecureResult = gson.fromJson(json, ThreeDSecureInfo.class);

        threeDSecureListener.onAuthorizationCompleted(threeDSecureResult, receiptId);
    }

    public void setResultPageListener(ThreeDSecureResultPageListener resultPageListener) {
        this.webViewClient.setResultPageListener(resultPageListener);
    }

}
package com.judopay.secure3d;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.judopay.BuildConfig;
import com.judopay.error.Show3dSecureWebViewError;
import com.judopay.model.ThreeDSecureInfo;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static java.net.URLEncoder.encode;

/**
 * A view that displays a 3D-Secure web page for performing additional security checks when validating
 * the payment method used by a customer. This WebView displays the page and then listens for when
 * the redirect URL is reached to obtain the payment data needed to finish the transaction.
 */
public class ThreeDSecureWebView extends WebView implements JsonParsingJavaScriptInterface.JsonListener {

    private static final String JS_NAMESPACE = "JudoPay";
    private static final String REDIRECT_URL = "https://pay.judopay.com/Android/Parse3DS";
    private static final String CHARSET = "UTF-8";

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

    @SuppressLint("AddJavascriptInterface")
    private void initialise() {
        configureSettings();

        if (BuildConfig.DEBUG && SDK_INT >= KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }

        addJavascriptInterface(new JsonParsingJavaScriptInterface(this), JS_NAMESPACE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureSettings() {
        WebSettings settings = getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
    }

    /**
     * @param acsUrl    URL to request in the WebView for displaying the 3D-Secure page to the user
     * @param md        parameter submitted to the acsUrl
     * @param paReq     parameter submitted to the acsUrl
     * @param receiptId the receipt ID of the transaction
     */
    public void authorize(final String acsUrl, final String md, final String paReq, String receiptId) {
        try {
            String postData = String.format(Locale.ENGLISH, "MD=%s&TermUrl=%s&PaReq=%s",
                    encode(md, CHARSET), encode(REDIRECT_URL, CHARSET), encode(paReq, CHARSET));

            this.receiptId = receiptId;

            this.webViewClient = new ThreeDSecureWebViewClient(REDIRECT_URL, JS_NAMESPACE, threeDSecureListener);
            setWebViewClient(webViewClient);

            postUrl(acsUrl, postData.getBytes());
        } catch (UnsupportedEncodingException e) {
            throw new Show3dSecureWebViewError(e);
        }
    }

    /**
     * @param threeDSecureListener listener that will be notified with authorization events
     */
    public void setThreeDSecureListener(ThreeDSecureListener threeDSecureListener) {
        this.threeDSecureListener = threeDSecureListener;
    }

    /**
     * @param json data returned from the redirected page containing JSON with the 3D-Secure result
     */
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
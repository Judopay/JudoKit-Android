package com.judopay.cardverification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.judopay.BuildConfig;
import com.judopay.error.Show3dSecureWebViewError;
import com.judopay.model.CardVerificationResult;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static java.net.URLEncoder.encode;

/**
 * A view that displays a 3D-Secure web page for performing additional security checks when validating
 * the payment method used by a customer. This WebView displays the page and then listens for when
 * the redirect URL is reached to obtain the payment data needed to finish the transaction.
 */
public class CardVerificationWebView extends WebView implements JsonParsingJavaScriptInterface.JsonListener {
    private static final String JS_NAMESPACE = "JudoPay";
    private static final String REDIRECT_URL = "https://pay.judopay.com/Android/Parse3DS";
    private static final String CHARSET = "UTF-8";

    private AuthorizationListener authorizationListener;
    private String receiptId;
    private WebViewListener resultPageListener;

    public CardVerificationWebView(Context context) {
        super(context);
        initialize();
    }

    public CardVerificationWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CardVerificationWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @SuppressLint("AddJavascriptInterface")
    private void initialize() {
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
            JsonRedirectWebViewClient webViewClient = new JsonRedirectWebViewClient(JS_NAMESPACE, CardVerificationWebView.REDIRECT_URL);
            webViewClient.setWebViewListener(resultPageListener);

            setWebViewClient(webViewClient);

            postUrl(acsUrl, postData.getBytes());
        } catch (UnsupportedEncodingException e) {
            throw new Show3dSecureWebViewError(e);
        }
    }

    /**
     * @param authorizationListener listener that will be notified with authorization events
     */
    public void setAuthorizationListener(AuthorizationListener authorizationListener) {
        this.authorizationListener = authorizationListener;
    }

    /**
     * @param json data returned from the redirected page containing JSON with the 3D-Secure result
     */
    @Override
    public void onJsonReceived(String json) {
        Gson gson = new Gson();

        CardVerificationResult cardVerificationResult = gson.fromJson(json, CardVerificationResult.class);
        authorizationListener.onAuthorizationCompleted(cardVerificationResult, receiptId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void setResultPageListener(WebViewListener resultPageListener) {
        this.resultPageListener = resultPageListener;
    }
}

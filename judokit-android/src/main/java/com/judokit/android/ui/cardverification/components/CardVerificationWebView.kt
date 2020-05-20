package com.judokit.android.ui.cardverification.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.KITKAT
import android.util.AttributeSet
import android.webkit.WebView
import com.google.gson.Gson
import com.judokit.android.BuildConfig
import com.judokit.android.api.model.response.CardVerificationResult
import com.judokit.android.ui.cardverification.CardVerificationWebViewClient
import com.judokit.android.ui.cardverification.WebViewCallback
import com.judokit.android.ui.cardverification.model.WebViewAction
import com.judokit.android.ui.error.Show3dSecureWebViewError
import java.io.UnsupportedEncodingException
import java.lang.String.format
import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets
import java.util.Locale

private const val JS_NAMESPACE = "JudoPay"
private const val REDIRECT_URL = "https://pay.judopay.com/Android/Parse3DS"
private const val CHARSET = "UTF-8"

class CardVerificationWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : WebView(context, attrs, defStyle), WebViewCallback {

    lateinit var view: WebViewCallback
    private lateinit var receiptId: String

    init {
        configureSettings()

        if (BuildConfig.DEBUG && SDK_INT >= KITKAT) {
            setWebContentsDebuggingEnabled(true)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun configureSettings() {
        settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        addJavascriptInterface(JsonParsingJavaScriptInterface { onJsonReceived(it) }, JS_NAMESPACE)
    }

    /**
     * @param acsUrl URL to request in the WebView for displaying the 3D-Secure page to the user
     * @param md parameter submitted to the acsUrl
     * @param paReq parameter submitted to the acsUrl
     * @param receiptId the receipt ID of the transaction
     */
    fun authorize(
        acsUrl: String?,
        md: String?,
        paReq: String?,
        receiptId: String?
    ) {
        try {
            val postData: String = format(
                Locale.ENGLISH, "MD=%s&TermUrl=%s&PaReq=%s",
                encode(md, CHARSET), encode(REDIRECT_URL, CHARSET), encode(paReq, CHARSET)
            )
            this.receiptId = receiptId ?: ""
            val webViewClient = CardVerificationWebViewClient(JS_NAMESPACE, REDIRECT_URL)
            setWebViewClient(webViewClient)
            postUrl(acsUrl, postData.toByteArray(StandardCharsets.UTF_8))
        } catch (throwable: UnsupportedEncodingException) {
            throw Show3dSecureWebViewError(throwable)
        }
    }

    private fun onJsonReceived(json: String) {
        val cardVerificationResult = Gson().fromJson(json, CardVerificationResult::class.java)
        view.send(WebViewAction.OnAuthorizationComplete(cardVerificationResult, receiptId))
    }

    override fun send(action: WebViewAction) {
        view.send(action)
    }
}

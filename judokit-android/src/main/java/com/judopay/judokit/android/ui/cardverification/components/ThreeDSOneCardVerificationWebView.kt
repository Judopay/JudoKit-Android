package com.judopay.judokit.android.ui.cardverification.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.KITKAT
import android.util.AttributeSet
import android.webkit.WebView
import com.google.gson.Gson
import com.judopay.judokit.android.BuildConfig
import com.judopay.judokit.android.api.model.response.CardVerificationResult
import com.judopay.judokit.android.model.CardVerificationModel
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCardVerificationWebViewClient
import com.judopay.judokit.android.ui.cardverification.WebViewCallback
import com.judopay.judokit.android.ui.cardverification.model.WebViewAction
import com.judopay.judokit.android.ui.error.Show3dSecureWebViewError
import java.io.UnsupportedEncodingException
import java.lang.String.format
import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets
import java.util.Locale

private const val JS_NAMESPACE = "JudoPay"
private const val REDIRECT_URL = "https://pay.judopay.com/Android/Parse3DS"
private const val CHARSET = "UTF-8"

internal class CardVerificationWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : WebView(context, attrs, defStyle),
    WebViewCallback {

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
     * @param model Model that contains all the necessary parameters to pass 3D-Secure authentication.
     */
    fun authorize(model: CardVerificationModel) {
        try {
            val postData: String = format(
                Locale.ENGLISH, "MD=%s&TermUrl=%s&PaReq=%s",
                encode(model.md, CHARSET), encode(REDIRECT_URL, CHARSET), encode(model.paReq, CHARSET)
            )
            this.receiptId = model.receiptId
            val webViewClient = ThreeDSOneCardVerificationWebViewClient(JS_NAMESPACE, REDIRECT_URL)
            setWebViewClient(webViewClient)
            postUrl(model.acsUrl, postData.toByteArray(StandardCharsets.UTF_8))
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

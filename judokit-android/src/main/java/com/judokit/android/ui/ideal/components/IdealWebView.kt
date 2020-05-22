package com.judokit.android.ui.ideal.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

interface IdealWebViewCallback {
    fun onPageStarted(checksum: String)
}

class IdealWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : WebView(context, attrs, defStyle), IdealWebViewCallback {

    lateinit var view: IdealWebViewCallback

    init {
        configureSettings()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureSettings() {
        settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }
    }

    fun authorize(redirectUrl: String, merchantRedirectUrl: String) {
        loadUrl(redirectUrl)
        val webViewClient = IdealWebViewClient(merchantRedirectUrl)
        setWebViewClient(webViewClient)
    }

    override fun onPageStarted(checksum: String) {
        view.onPageStarted(checksum)
    }
}

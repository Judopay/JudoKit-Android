package com.judokit.android.ui.ideal.components

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri

private const val CHECKSUM_DELIMITER = "cs="

class IdealWebViewClient(private val merchantRedirectUrl: String) :
    WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        val merchantRedirectHost = merchantRedirectUrl.toUri().host
        if (!url.isNullOrEmpty()) {
            val urlHost = url.toUri().host
            if (
                !merchantRedirectHost.isNullOrEmpty() &&
                !urlHost.isNullOrEmpty() &&
                urlHost.contains(merchantRedirectHost)
            ) {
                val urlSplit = url.split(CHECKSUM_DELIMITER)
                if (urlSplit.isNotEmpty() && urlSplit.size == 2) {
                    val checksum = urlSplit[1]
                    (view as IdealWebViewCallback).onPageStarted(checksum)
                }
            }
        }
    }
}

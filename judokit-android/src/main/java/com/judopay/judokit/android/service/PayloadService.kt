package com.judopay.judokit.android.service

import android.content.Context
import android.webkit.WebSettings
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.Browser
import com.judopay.judokit.android.api.model.ConsumerDevice
import com.judopay.judokit.android.api.model.EnhancedPaymentDetail
import com.judopay.judokit.android.api.model.SDKInfo
import com.judopay.judokit.android.api.model.ThreeDSecure
import com.judopay.judokit.android.ui.common.JUDO_KIT_VERSION

class PayloadService(
    private val context: Context,
) {
    fun getEnhancedPaymentDetail(): EnhancedPaymentDetail = EnhancedPaymentDetail(getSdkInfo(), getConsumerDevice())

    private fun getSdkInfo(): SDKInfo = SDKInfo(JUDO_KIT_VERSION, context.getString(R.string.jp_judokit_android))

    private fun getConsumerDevice(): ConsumerDevice = ConsumerDevice(getThreeDSecureInfo())

    private fun getThreeDSecureInfo(): ThreeDSecure = ThreeDSecure(getBrowserInfo())

    @Suppress("SwallowedException")
    private fun getBrowserInfo(): Browser = Browser(WebSettings.getDefaultUserAgent(context))
}

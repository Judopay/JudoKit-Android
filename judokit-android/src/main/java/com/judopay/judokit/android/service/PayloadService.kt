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

    private fun getBrowserInfo(): Browser {
        // getDefaultUserAgent requires an Activity context; System.getProperty may throw
        // SecurityException in restricted profiles — both are caught, empty string is the last resort.
        val userAgent =
            runCatching { WebSettings.getDefaultUserAgent(context) }
                .recoverCatching { System.getProperty("http.agent") ?: "" }
                .getOrDefault("")
        return Browser(userAgent)
    }
}

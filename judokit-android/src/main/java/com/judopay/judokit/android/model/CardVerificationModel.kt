package com.judopay.judokit.android.model

import android.os.Parcelable
import com.judopay.judokit.android.requireNotNullOrEmpty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardVerificationModel internal constructor(
    var receiptId: String,
    var md: String,
    var paReq: String,
    var acsUrl: String
) : Parcelable {
    class Builder {
        private var receiptId: String? = null
        private var md: String? = null
        private var paReq: String? = null
        private var acsUrl: String? = null

        fun setReceiptId(receiptId: String?) = apply { this.receiptId = receiptId }
        fun setMd(md: String?) = apply { this.md = md }
        fun setPaReq(paReq: String?) = apply { this.paReq = paReq }
        fun setAcsUrl(acsUrl: String?) = apply { this.acsUrl = acsUrl }

        @Throws(IllegalArgumentException::class)
        fun build(): CardVerificationModel {
            val myReceiptId = requireNotNullOrEmpty(receiptId, "receiptId")
            val myMd = requireNotNullOrEmpty(md, "md")
            val myPaReq = requireNotNullOrEmpty(paReq, "paReq")
            val myAcsUrl = requireNotNullOrEmpty(acsUrl, "acsUrl")

            return CardVerificationModel(myReceiptId, myMd, myPaReq, myAcsUrl)
        }
    }
}

package com.judopay.judokit.android.model

import android.os.Parcelable
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.api.model.response.Consumer
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.Date

/**
 * The successful result of a transaction performed within Judo SDK.
 */
@Parcelize
data class JudoResult(
    val judoId: String? = null,
    val receiptId: String? = null,
    val originalReceiptId: String? = null,
    val partnerServiceFee: String? = null,
    val yourPaymentReference: String? = null,
    val type: String? = null,
    val createdAt: Date? = null,
    val merchantName: String? = null,
    val appearsOnStatementAs: String? = null,
    val originalAmount: BigDecimal? = null,
    val netAmount: BigDecimal? = null,
    val amount: BigDecimal? = null,
    val currency: String? = null,
    val cardDetails: CardToken? = null,
    val consumer: Consumer? = null,
    val result: String? = null,
    val message: String? = null,
    val yourPaymentMetaData: Map<String, String>? = null,
    val emailAddress: String? = null,
) : Parcelable

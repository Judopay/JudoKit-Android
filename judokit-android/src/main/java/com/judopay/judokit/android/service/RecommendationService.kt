package com.judopay.judokit.android.service

import android.content.Context
import android.os.Build
import com.google.gson.JsonSyntaxException
import com.judopay.judo3ds2.exception.SDKRuntimeException
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.RecommendationApiService
import com.judopay.judokit.android.api.factory.RecommendationApiServiceFactory
import com.judopay.judokit.android.api.model.request.recommendation.PaymentMethodCipher
import com.judopay.judokit.android.api.model.request.recommendation.RecommendationPaymentMethod
import com.judopay.judokit.android.api.model.request.recommendation.RecommendationRequest
import com.judopay.judokit.android.api.model.response.recommendation.RecommendationResponse
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.ui.common.isDependencyPresent
import com.ravelin.cardEncryption.RavelinEncrypt
import com.ravelin.cardEncryption.model.CardDetails
import com.ravelin.cardEncryption.model.EncryptedCard
import retrofit2.Call

const val RAVELIN_ENCRYPT_CLASS_NAME = "com.ravelin.cardEncryption.RavelinEncrypt"

@Suppress("ReturnCount")
private fun TransactionDetails.toRavelinEncryptedCard(rsaPublicKey: String): EncryptedCard? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
        return null
    }

    val pan = cardNumber
    val dateComponents = expirationDate?.split("/") ?: emptyList()
    val month = dateComponents.getOrNull(0)
    val year = dateComponents.getOrNull(1)
    val nameOnCard = cardHolderName

    if (pan.isNullOrEmpty() || month.isNullOrEmpty() || year.isNullOrEmpty()) {
        return null
    }

    val cardDetails = CardDetails(pan, month, year, nameOnCard)
    return RavelinEncrypt().encryptCard(cardDetails, rsaPublicKey)
}

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
private fun EncryptedCard.toRecommendationRequest() =
    RecommendationRequest
        .Builder()
        .setPaymentMethod(
            paymentMethod =
                RecommendationPaymentMethod(
                    paymentMethodCipher =
                        PaymentMethodCipher(
                            aesKeyCipherText = aesKeyCiphertext,
                            algorithm = algorithm,
                            cardCipherText = cardCiphertext,
                            keyIndex = keyIndex,
                            keySignature = "key-signature",
                            methodType = "paymentMethodCipher",
                            recommendationFeatureProviderSDKVersion = ravelinSDKVersion,
                        ),
                ),
        ).build()

class RecommendationService(
    private val context: Context,
    private val judo: Judo,
) {
    private val apiService: RecommendationApiService by lazy {
        RecommendationApiServiceFactory.create(context, judo)
    }

    fun isRecommendationFeatureAvailable(type: TransactionType): Boolean {
        val isSupportedType = type == TransactionType.PAYMENT || type == TransactionType.CHECK || type == TransactionType.PRE_AUTH

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 &&
            // RavelinEncrypt requires API 22
            isDependencyPresent(RAVELIN_ENCRYPT_CLASS_NAME) &&
            // RavelinEncrypt should be present in classpath
            judo.recommendationConfiguration != null &&
            // recommendationConfiguration should be set by the user
            isSupportedType // recommendation is only supported for payment, check and pre-auth transactions
    }

    fun fetchOptimizationData(
        details: TransactionDetails,
        type: TransactionType,
    ): Call<RecommendationResponse> {
        check(isRecommendationFeatureAvailable(type)) { "Recommendation feature is not available." }

        val config = judo.recommendationConfiguration
        check(config != null) { "Recommendation configuration is not set. Cannot create recommendation request." }

        val request = details.toRavelinEncryptedCard(config.rsaPublicKey)?.toRecommendationRequest()
        check(request != null) { "Invalid transaction details. Cannot create recommendation request." }

        return apiService.requestRecommendation(config.url, request)
    }
}

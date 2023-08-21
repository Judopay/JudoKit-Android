package com.judopay.judokit.android.model

import com.google.gson.JsonSyntaxException
import com.judopay.judo3ds2.exception.SDKRuntimeException
import com.judopay.judokit.android.api.model.request.RecommendationRequest
import com.ravelin.cardEncryption.model.EncryptedCard

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
fun EncryptedCard.toRecommendationRequest() = RecommendationRequest
    .Builder()
    // Todo: Validation!
    .setPaymentMethod(
        paymentMethod = RecommendationPaymentMethod(
            paymentMethodCipher = PaymentMethodCipher(
            aesKeyCipherText = aesKeyCiphertext,
            algorithm = algorithm,
            cardCipherText = cardCiphertext,
            keyIndex = keyIndex,
            // Todo: What about these two properties below?
            keySignature = "key-signature",
            methodType = "paymentMethodCipher",
            ravelinSDKVersion = ravelinSDKVersion
        ))
    )
    .build()

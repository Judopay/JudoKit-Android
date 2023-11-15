package com.judopay.judokit.android.api.model.request.threedsecure

import com.google.gson.annotations.SerializedName
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption
import com.judopay.judokit.android.requireNotNull

class ThreeDSecureTwo private constructor(
    private val challengeRequestIndicator: ChallengeRequestIndicator?,
    private val scaExemption: ScaExemption?,
    @SerializedName("sdk")
    private val sdkParameters: SdkParameters,
    private val authenticationSource: String = "MOBILE_SDK",
    private val softDeclineReceiptId: String?
) {
    class Builder {
        private var challengeRequestIndicator: ChallengeRequestIndicator? = null
        private var scaExemption: ScaExemption? = null
        private var sdkParameters: SdkParameters? = null
        private var softDeclineReceiptId: String? = null

        fun setChallengeRequestIndicator(challengeRequestIndicator: ChallengeRequestIndicator?) =
            apply { this.challengeRequestIndicator = challengeRequestIndicator }

        fun setScaExemption(scaExemption: ScaExemption?) =
            apply { this.scaExemption = scaExemption }

        fun setSdkParameters(sdkParameters: SdkParameters?) =
            apply { this.sdkParameters = sdkParameters }

        fun setSoftDeclineReceiptId(softDeclineReceiptId: String?) =
            apply { this.softDeclineReceiptId = softDeclineReceiptId }

        fun build(): ThreeDSecureTwo {
            val myChallengeRequestIndicator = challengeRequestIndicator
            val myScaExemption = scaExemption
            val mySdkParameters = requireNotNull(sdkParameters, "sdkParameters")
            val mySoftDeclineReceiptId = softDeclineReceiptId

            return ThreeDSecureTwo(
                myChallengeRequestIndicator,
                myScaExemption,
                mySdkParameters,
                softDeclineReceiptId = mySoftDeclineReceiptId
            )
        }
    }
}

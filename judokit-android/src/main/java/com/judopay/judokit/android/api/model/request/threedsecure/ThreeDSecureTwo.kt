package com.judopay.judokit.android.api.model.request.threedsecure

import com.google.gson.annotations.SerializedName
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption
import com.judopay.judokit.android.requireNotNull

class ThreeDSecureTwo private constructor(
    private val challengeRequestIndicator: ChallengeRequestIndicator,
    private val scaExemption: ScaExemption,
    @SerializedName("sdk")
    private val sdkParameters: SdkParameters,
    private val authenticationSource: String = "MOBILE_SDK"
) {
    class Builder {
        private var challengeRequestIndicator: ChallengeRequestIndicator? = ChallengeRequestIndicator.CHALLENGE_AS_MANDATE
        private var scaExemption: ScaExemption? = null
        private var sdkParameters: SdkParameters? = null

        fun setChallengeRequestIndicator(challengeRequestIndicator: ChallengeRequestIndicator?) =
            apply { this.challengeRequestIndicator = challengeRequestIndicator }

        fun setScaExemption(scaExemption: ScaExemption?) =
            apply { this.scaExemption = scaExemption }

        fun setSdkParameters(sdkParameters: SdkParameters?) =
            apply { this.sdkParameters = sdkParameters }

        fun build(): ThreeDSecureTwo {
            val myChallengeRequestIndicator =
                requireNotNull(challengeRequestIndicator, "challengeRequestIndicator")
            val myScaExemption = requireNotNull(scaExemption, "scaExemption")
            val mySdkParameters = requireNotNull(sdkParameters, "sdkParameters")

            return ThreeDSecureTwo(myChallengeRequestIndicator, myScaExemption, mySdkParameters)
        }
    }
}

package com.judopay.judokit.android.api.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ThreeDSecure(
    val attempted: Boolean? = null,
    val result: String? = null,
    val eci: String? = null,
    val challengeRequestIndicator: String? = null,
    val challengeCompleted: Boolean? = null,
) : Parcelable {
    override fun toString(): String {
        return """
            ThreeDSecure(
            attempted: $attempted,
            result: $result,
            eci: $eci,
            challengeRequestIndicator: $challengeRequestIndicator,
            challengeCompleted: $challengeCompleted)
        """
    }
}

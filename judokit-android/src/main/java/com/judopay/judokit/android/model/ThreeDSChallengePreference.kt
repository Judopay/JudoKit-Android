package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ThreeDSChallengePreference : Parcelable {
    NO_PREFERENCE,
    NO_CHALLENGE_REQUESTED,
    CHALLENGE_REQUESTED,
    CHALLENGE_REQUESTED_AS_MANDATE
}

fun ThreeDSChallengePreference.toChallengeRequestIndicator(): ChallengeRequestIndicator {
    return when (this) {
        ThreeDSChallengePreference.NO_PREFERENCE -> ChallengeRequestIndicator.NO_PREFERENCE
        ThreeDSChallengePreference.NO_CHALLENGE_REQUESTED -> ChallengeRequestIndicator.NO_CHALLENGE
        ThreeDSChallengePreference.CHALLENGE_REQUESTED -> ChallengeRequestIndicator.CHALLENGE_PREFERRED
        ThreeDSChallengePreference.CHALLENGE_REQUESTED_AS_MANDATE -> ChallengeRequestIndicator.CHALLENGE_AS_MANDATE
    }
}

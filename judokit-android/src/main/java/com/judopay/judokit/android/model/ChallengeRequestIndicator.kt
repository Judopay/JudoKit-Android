package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ChallengeRequestIndicator(val value: String) : Parcelable {
    NO_PREFERENCE("noPreference"),
    NO_CHALLENGE("noChallenge"),
    CHALLENGE_PREFERRED("challengePreferred"),
    CHALLENGE_AS_MANDATE("challengeAsMandate")
}

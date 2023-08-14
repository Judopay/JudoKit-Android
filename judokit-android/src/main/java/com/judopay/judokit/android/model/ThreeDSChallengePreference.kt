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

package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.ChallengeRequestIndicator

data class StepUpFlowDetails(
    val softDeclineReceiptId: String,
    val challengeRequestIndicator: ChallengeRequestIndicator = ChallengeRequestIndicator.CHALLENGE_AS_MANDATE
)

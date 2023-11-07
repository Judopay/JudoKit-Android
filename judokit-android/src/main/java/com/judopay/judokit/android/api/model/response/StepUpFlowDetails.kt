package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.ChallengeRequestIndicator

data class StepUpFlowDetails(
    // Todo: Confirm with Stefan whether we want these two to be nullable.
    val challengeRequestIndicator: ChallengeRequestIndicator?,
    val softDeclineReceiptId: String?
)
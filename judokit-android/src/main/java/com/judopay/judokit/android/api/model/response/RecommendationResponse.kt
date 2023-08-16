package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.JudoResult

data class RecommendationResponse(
    val data: RecommendationData
)

fun RecommendationResponse.toJudoResult() = JudoResult(
    // Todo: What data could/ should we put here?
)

package com.judopay.judokit.android.api.model.response.recommendation

private const val ALLOW = "ALLOW"
private const val REVIEW = "REVIEW"
private const val PREVENT = "PREVENT"

fun String?.toRecommendationAction(): RecommendationAction? {
    return when (this?.uppercase()) {
        ALLOW -> RecommendationAction.ALLOW
        REVIEW -> RecommendationAction.REVIEW
        PREVENT -> RecommendationAction.PREVENT
        null -> null
        else -> RecommendationAction.ERROR_ACTION_NOT_RECOGNIZED
    }
}
data class RecommendationData(
    val action: String?,
    val transactionOptimisation: TransactionOptimisation?
) {
    val isValid: Boolean
        get() {
            return (action != null &&
                    action.toRecommendationAction() != RecommendationAction.ERROR_ACTION_NOT_RECOGNIZED &&
                    transactionOptimisation?.isValid != false)
        }
}

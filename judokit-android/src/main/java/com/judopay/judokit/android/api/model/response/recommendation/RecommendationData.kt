package com.judopay.judokit.android.api.model.response.recommendation

data class RecommendationData(
    val action: RecommendationAction?,
    val transactionOptimisation: TransactionOptimisation?
) {
    val isValid: Boolean
        get() {
            return (action != null)
        }
}

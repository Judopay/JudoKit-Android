package com.judopay.judokit.android.api.model.response.recommendation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing RecommendationData")
internal class RecommendationDataTest {
    @Test
    @DisplayName("Given null action, then isValid returns false")
    fun isValidReturnsFalseForNullAction() {
        val data = RecommendationData(action = null, transactionOptimisation = null)
        assertFalse(data.isValid)
    }

    @Test
    @DisplayName("Given non-null action and null transactionOptimisation, then isValid returns true")
    fun isValidReturnsTrueForActionWithNoOptimisation() {
        val data = RecommendationData(action = RecommendationAction.ALLOW, transactionOptimisation = null)
        assertTrue(data.isValid)
    }

    @Test
    @DisplayName("Given non-null action and valid transactionOptimisation, then isValid returns true")
    fun isValidReturnsTrueForActionWithValidOptimisation() {
        val optimisation = TransactionOptimisation(exemption = "LOW_VALUE", threeDSChallengePreference = null)
        val data = RecommendationData(action = RecommendationAction.ALLOW, transactionOptimisation = optimisation)
        assertTrue(data.isValid)
    }

    @Test
    @DisplayName("Given non-null action and invalid transactionOptimisation, then isValid returns false")
    fun isValidReturnsFalseForActionWithInvalidOptimisation() {
        val optimisation = TransactionOptimisation(exemption = "INVALID", threeDSChallengePreference = null)
        val data = RecommendationData(action = RecommendationAction.ALLOW, transactionOptimisation = optimisation)
        assertFalse(data.isValid)
    }
}

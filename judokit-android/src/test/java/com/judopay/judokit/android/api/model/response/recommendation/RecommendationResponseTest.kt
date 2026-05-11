package com.judopay.judokit.android.api.model.response.recommendation

import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing RecommendationResponse")
internal class RecommendationResponseTest {
    @Test
    @DisplayName("Given null data, then isValid returns false")
    fun isValidReturnsFalseForNullData() {
        val response = RecommendationResponse(data = null)
        assertFalse(response.isValid)
    }

    @Test
    @DisplayName("Given valid data, then isValid returns true")
    fun isValidReturnsTrueForValidData() {
        val data = RecommendationData(action = RecommendationAction.ALLOW, transactionOptimisation = null)
        val response = RecommendationResponse(data = data)
        assertTrue(response.isValid)
    }

    @Test
    @DisplayName("Given invalid response, then toTransactionDetailsOverrides returns null")
    fun toTransactionDetailsOverridesReturnsNullForInvalidResponse() {
        val response = RecommendationResponse(data = null)
        assertNull(response.toTransactionDetailsOverrides())
    }

    @Test
    @DisplayName("Given valid response with exemption and challenge preference, then toTransactionDetailsOverrides maps both fields")
    fun toTransactionDetailsOverridesMapsFields() {
        val optimisation =
            TransactionOptimisation(
                exemption = "LOW_VALUE",
                threeDSChallengePreference = "NO_PREFERENCE",
            )
        val data = RecommendationData(action = RecommendationAction.ALLOW, transactionOptimisation = optimisation)
        val response = RecommendationResponse(data = data)
        val result = response.toTransactionDetailsOverrides()
        assertNotNull(result)
        assertEquals(ScaExemption.LOW_VALUE, result?.exemption)
        assertEquals(ChallengeRequestIndicator.NO_PREFERENCE, result?.challengeRequestIndicator)
    }

    @Test
    @DisplayName("Given valid response with invalid exemption, then toTransactionDetailsOverrides returns result with null exemption")
    fun toTransactionDetailsOverridesHandlesInvalidExemption() {
        val optimisation =
            TransactionOptimisation(
                exemption = "INVALID_EXEMPTION",
                threeDSChallengePreference = "NO_PREFERENCE",
            )
        val data = RecommendationData(action = RecommendationAction.ALLOW, transactionOptimisation = optimisation)
        val response = RecommendationResponse(data = data)
        assertNull(response.toTransactionDetailsOverrides())
    }

    @Test
    @DisplayName(
        "Given valid response with null optimisation fields, then toTransactionDetailsOverrides returns overrides with null fields",
    )
    fun toTransactionDetailsOverridesHandlesNullFields() {
        val data = RecommendationData(action = RecommendationAction.PREVENT, transactionOptimisation = null)
        val response = RecommendationResponse(data = data)
        val result = response.toTransactionDetailsOverrides()
        assertNotNull(result)
        assertNull(result?.exemption)
        assertNull(result?.challengeRequestIndicator)
    }
}

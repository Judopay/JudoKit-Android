package com.judopay.judokit.android.api.model.response.recommendation

import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing TransactionOptimisation")
internal class TransactionOptimisationTest {
    @Test
    @DisplayName("Given valid exemption and challenge preference, then isValid returns true")
    fun isValidReturnsTrueForValidFields() {
        val optimisation =
            TransactionOptimisation(
                exemption = "LOW_VALUE",
                threeDSChallengePreference = "NO_PREFERENCE",
            )
        assertTrue(optimisation.isValid)
    }

    @Test
    @DisplayName("Given null exemption and null challenge preference, then isValid returns true")
    fun isValidReturnsTrueForNullFields() {
        val optimisation =
            TransactionOptimisation(
                exemption = null,
                threeDSChallengePreference = null,
            )
        assertTrue(optimisation.isValid)
    }

    @Test
    @DisplayName("Given invalid exemption, then isValid returns false")
    fun isValidReturnsFalseForInvalidExemption() {
        val optimisation =
            TransactionOptimisation(
                exemption = "INVALID_EXEMPTION",
                threeDSChallengePreference = null,
            )
        assertFalse(optimisation.isValid)
    }

    @Test
    @DisplayName("Given invalid challenge preference, then isValid returns false")
    fun isValidReturnsFalseForInvalidChallengePreference() {
        val optimisation =
            TransactionOptimisation(
                exemption = null,
                threeDSChallengePreference = "INVALID_PREFERENCE",
            )
        assertFalse(optimisation.isValid)
    }

    @Test
    @DisplayName("Given 'NO_PREFERENCE', then toChallengeRequestIndicator returns NO_PREFERENCE")
    fun toChallengeRequestIndicatorNoPreference() {
        assertEquals(ChallengeRequestIndicator.NO_PREFERENCE, "NO_PREFERENCE".toChallengeRequestIndicator())
    }

    @Test
    @DisplayName("Given 'NO_CHALLENGE_REQUESTED', then toChallengeRequestIndicator returns NO_CHALLENGE")
    fun toChallengeRequestIndicatorNoChallenge() {
        assertEquals(ChallengeRequestIndicator.NO_CHALLENGE, "NO_CHALLENGE_REQUESTED".toChallengeRequestIndicator())
    }

    @Test
    @DisplayName("Given 'CHALLENGE_REQUESTED', then toChallengeRequestIndicator returns CHALLENGE_PREFERRED")
    fun toChallengeRequestIndicatorChallengePreferred() {
        assertEquals(ChallengeRequestIndicator.CHALLENGE_PREFERRED, "CHALLENGE_REQUESTED".toChallengeRequestIndicator())
    }

    @Test
    @DisplayName("Given 'CHALLENGE_REQUESTED_AS_MANDATE', then toChallengeRequestIndicator returns CHALLENGE_AS_MANDATE")
    fun toChallengeRequestIndicatorChallengeAsMandate() {
        assertEquals(ChallengeRequestIndicator.CHALLENGE_AS_MANDATE, "CHALLENGE_REQUESTED_AS_MANDATE".toChallengeRequestIndicator())
    }

    @Test
    @DisplayName("Given null, then toChallengeRequestIndicator returns null")
    fun toChallengeRequestIndicatorNull() {
        assertNull(null.toChallengeRequestIndicator())
    }

    @Test
    @DisplayName("Given an invalid string, then toChallengeRequestIndicator throws IllegalArgumentException")
    fun toChallengeRequestIndicatorThrowsForInvalid() {
        assertThrows<IllegalArgumentException> {
            "INVALID".toChallengeRequestIndicator()
        }
    }

    @Test
    @DisplayName("Given 'LOW_VALUE', then toScaExemption returns LOW_VALUE")
    fun toScaExemptionLowValue() {
        assertEquals(ScaExemption.LOW_VALUE, "LOW_VALUE".toScaExemption())
    }

    @Test
    @DisplayName("Given 'TRANSACTION_RISK_ANALYSIS', then toScaExemption returns TRANSACTION_RISK_ANALYSIS")
    fun toScaExemptionTransactionRiskAnalysis() {
        assertEquals(ScaExemption.TRANSACTION_RISK_ANALYSIS, "TRANSACTION_RISK_ANALYSIS".toScaExemption())
    }

    @Test
    @DisplayName("Given null, then toScaExemption returns null")
    fun toScaExemptionNull() {
        assertNull(null.toScaExemption())
    }

    @Test
    @DisplayName("Given an invalid string, then toScaExemption throws IllegalArgumentException")
    fun toScaExemptionThrowsForInvalid() {
        assertThrows<IllegalArgumentException> {
            "INVALID".toScaExemption()
        }
    }

    @Test
    @DisplayName("Given lowercase 'low_value', then toScaExemption returns LOW_VALUE (case insensitive)")
    fun toScaExemptionLowValueCaseInsensitive() {
        assertEquals(ScaExemption.LOW_VALUE, "low_value".toScaExemption())
    }
}

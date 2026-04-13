package com.judopay.judokit.android.service

import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing TransactionType")
internal class TransactionTypeTest {
    @Test
    @DisplayName("TransactionType has all expected values")
    fun transactionTypeHasAllValues() {
        val values = TransactionType.values()
        assertTrue(values.contains(TransactionType.PAYMENT))
        assertTrue(values.contains(TransactionType.PRE_AUTH))
        assertTrue(values.contains(TransactionType.PAYMENT_WITH_TOKEN))
        assertTrue(values.contains(TransactionType.PRE_AUTH_WITH_TOKEN))
        assertTrue(values.contains(TransactionType.SAVE))
        assertTrue(values.contains(TransactionType.CHECK))
    }

    @Test
    @DisplayName("All TransactionType values can be enumerated")
    fun transactionTypeCount() {
        assertTrue(TransactionType.values().size == 6)
    }

    @Test
    @DisplayName("ChallengeData stores transaction and challengeParameters")
    fun challengeDataStoresFields() {
        val transaction = mockk<Transaction>()
        val params = mockk<ChallengeParameters>()
        val data = ChallengeData(transaction, params)
        assertEquals(transaction, data.transaction)
        assertEquals(params, data.challengeParameters)
    }

    @Test
    @DisplayName("ChallengeData with identical fields are equal and share the same hashCode")
    fun challengeDataEqualsAndHashCode() {
        val transaction = mockk<Transaction>()
        val params = mockk<ChallengeParameters>()
        val data1 = ChallengeData(transaction, params)
        val data2 = ChallengeData(transaction, params)
        assertEquals(data1, data2)
        assertEquals(data1.hashCode(), data2.hashCode())
    }

    @Test
    @DisplayName("ChallengeData toString includes class name")
    fun challengeDataToStringContainsClassName() {
        val transaction = mockk<Transaction>()
        val params = mockk<ChallengeParameters>()
        val data = ChallengeData(transaction, params)
        assertTrue(data.toString().contains("ChallengeData"))
    }
}

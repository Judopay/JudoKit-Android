package com.judopay.judokit.android.service

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
}

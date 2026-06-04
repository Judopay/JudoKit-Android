package com.judopay.judokit.android.ui.paymentmethods.adapter.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Test PaymentMethodDiffUtil")
internal class PaymentMethodDiffUtilTest {
    private fun headerItem(isInEditMode: Boolean = false) =
        PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = isInEditMode)

    private fun footerItem() = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_FOOTER, isInEditMode = false)

    @Test
    @DisplayName("Given old and new lists, then getOldListSize returns old list size")
    fun getOldListSizeReturnsCorrectSize() {
        val sut =
            PaymentMethodDiffUtil(
                oldList = listOf(headerItem(), footerItem()),
                newList = listOf(headerItem()),
            )
        assertEquals(2, sut.oldListSize)
    }

    @Test
    @DisplayName("Given old and new lists, then getNewListSize returns new list size")
    fun getNewListSizeReturnsCorrectSize() {
        val sut =
            PaymentMethodDiffUtil(
                oldList = listOf(headerItem()),
                newList = listOf(headerItem(), footerItem()),
            )
        assertEquals(2, sut.newListSize)
    }

    @Test
    @DisplayName("Given items with same type at same position, then areItemsTheSame returns true")
    fun areItemsTheSameReturnsTrueForSameType() {
        val sut =
            PaymentMethodDiffUtil(
                oldList = listOf(headerItem()),
                newList = listOf(headerItem()),
            )
        assertTrue(sut.areItemsTheSame(0, 0))
    }

    @Test
    @DisplayName("Given items with different types at same position, then areItemsTheSame returns false")
    fun areItemsTheSameReturnsFalseForDifferentType() {
        val sut =
            PaymentMethodDiffUtil(
                oldList = listOf(headerItem()),
                newList = listOf(footerItem()),
            )
        assertFalse(sut.areItemsTheSame(0, 0))
    }

    @Test
    @DisplayName("Given identical items, then areContentsTheSame returns true")
    fun areContentsTheSameReturnsTrueForIdenticalItems() {
        val item = headerItem(isInEditMode = false)
        val sut =
            PaymentMethodDiffUtil(
                oldList = listOf(item),
                newList = listOf(item),
            )
        assertTrue(sut.areContentsTheSame(0, 0))
    }

    @Test
    @DisplayName("Given items with same type but different content, then areContentsTheSame returns false")
    fun areContentsTheSameReturnsFalseForDifferentContent() {
        val sut =
            PaymentMethodDiffUtil(
                oldList = listOf(headerItem(isInEditMode = false)),
                newList = listOf(headerItem(isInEditMode = true)),
            )
        assertFalse(sut.areContentsTheSame(0, 0))
    }

    @Test
    @DisplayName("Given empty lists, then list sizes are both zero")
    fun emptyListsHaveSizeZero() {
        val sut = PaymentMethodDiffUtil(oldList = emptyList(), newList = emptyList())
        assertEquals(0, sut.oldListSize)
        assertEquals(0, sut.newListSize)
    }
}

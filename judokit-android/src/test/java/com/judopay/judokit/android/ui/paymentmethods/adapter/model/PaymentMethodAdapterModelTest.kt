package com.judopay.judokit.android.ui.paymentmethods.adapter.model

import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.ui.editcard.CardPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing PaymentMethod adapter model classes")
internal class PaymentMethodAdapterModelTest {
    private fun savedCardItem(
        id: Int = 1,
        isSelected: Boolean = false,
        isInEditMode: Boolean = false,
    ) = PaymentMethodSavedCardItem(
        id = id,
        title = "Visa",
        network = CardNetwork.VISA,
        ending = "1234",
        token = "tok_abc",
        expireDate = "12/25",
        cardholderName = "John Doe",
        isSelected = isSelected,
        isInEditMode = isInEditMode,
        pattern = CardPattern.BLACK,
    )

    @Test
    @DisplayName("PaymentMethodSavedCardItem equality when fields match")
    fun savedCardItemEqualityWhenFieldsMatch() {
        val item1 = savedCardItem(id = 1)
        val item2 = savedCardItem(id = 1)
        assertEquals(item1, item2)
    }

    @Test
    @DisplayName("PaymentMethodSavedCardItem inequality when id differs")
    fun savedCardItemInequalityWhenIdDiffers() {
        val item1 = savedCardItem(id = 1)
        val item2 = savedCardItem(id = 2)
        assertNotEquals(item1, item2)
    }

    @Test
    @DisplayName("PaymentMethodSavedCardItem not equal to null")
    fun savedCardItemNotEqualToNull() {
        assertFalse(savedCardItem() == null)
    }

    @Test
    @DisplayName("PaymentMethodSavedCardItem not equal to different type")
    fun savedCardItemNotEqualToDifferentType() {
        assertFalse(savedCardItem().equals("string"))
    }

    @Test
    @DisplayName("PaymentMethodSavedCardItem hashCode is consistent")
    fun savedCardItemHashCodeConsistent() {
        val item = savedCardItem(id = 1)
        assertEquals(item.hashCode(), item.hashCode())
    }

    @Test
    @DisplayName("PaymentMethodSavedCardItem hashCode equals for equal items")
    fun savedCardItemHashCodeEqualsForEqualItems() {
        assertEquals(savedCardItem(id = 1).hashCode(), savedCardItem(id = 1).hashCode())
    }

    @Test
    @DisplayName("PaymentMethodSavedCardItem same reference equals itself")
    fun savedCardItemSameReferenceEqualsItself() {
        val item = savedCardItem()
        assertTrue(item == item)
    }

    @Test
    @DisplayName("PaymentMethodGenericItem equality when fields match")
    fun genericItemEquality() {
        val item1 = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = false)
        val item2 = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = false)
        assertEquals(item1, item2)
    }

    @Test
    @DisplayName("PaymentMethodGenericItem inequality when type differs")
    fun genericItemInequalityOnTypeDifference() {
        val item1 = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = false)
        val item2 = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_FOOTER, isInEditMode = false)
        assertNotEquals(item1, item2)
    }

    @Test
    @DisplayName("PaymentMethodGenericItem not equal to null")
    fun genericItemNotEqualToNull() {
        assertFalse(PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = false) == null)
    }

    @Test
    @DisplayName("PaymentMethodGenericItem not equal to different type")
    fun genericItemNotEqualToDifferentType() {
        assertFalse(PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = false).equals("string"))
    }

    @Test
    @DisplayName("PaymentMethodGenericItem same reference equals itself")
    fun genericItemSameReferenceEqualsItself() {
        val item = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = false)
        assertTrue(item == item)
    }

    @Test
    @DisplayName("PaymentMethodGenericItem hashCode equals for equal items")
    fun genericItemHashCodeEquals() {
        val item1 = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = true)
        val item2 = PaymentMethodGenericItem(type = PaymentMethodItemType.SAVED_CARDS_HEADER, isInEditMode = true)
        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    @DisplayName("PaymentMethodSelectorItem equality when payment methods match")
    fun selectorItemEquality() {
        val methods = listOf(PaymentMethod.CARD)
        val item1 = PaymentMethodSelectorItem(paymentMethods = methods, currentSelected = PaymentMethod.CARD)
        val item2 = PaymentMethodSelectorItem(paymentMethods = methods, currentSelected = PaymentMethod.CARD)
        assertEquals(item1, item2)
    }

    @Test
    @DisplayName("PaymentMethodSelectorItem inequality when payment methods differ")
    fun selectorItemInequalityOnMethodsDifference() {
        val item1 = PaymentMethodSelectorItem(paymentMethods = listOf(PaymentMethod.CARD), currentSelected = PaymentMethod.CARD)
        val item2 = PaymentMethodSelectorItem(paymentMethods = listOf(PaymentMethod.GOOGLE_PAY), currentSelected = PaymentMethod.GOOGLE_PAY)
        assertNotEquals(item1, item2)
    }

    @Test
    @DisplayName("PaymentMethodSelectorItem not equal to null")
    fun selectorItemNotEqualToNull() {
        val item = PaymentMethodSelectorItem(paymentMethods = emptyList(), currentSelected = PaymentMethod.CARD)
        assertFalse(item == null)
    }

    @Test
    @DisplayName("PaymentMethodSelectorItem not equal to different type")
    fun selectorItemNotEqualToDifferentType() {
        val item = PaymentMethodSelectorItem(paymentMethods = emptyList(), currentSelected = PaymentMethod.CARD)
        assertFalse(item.equals("string"))
    }

    @Test
    @DisplayName("PaymentMethodSelectorItem same reference equals itself")
    fun selectorItemSameReferenceEqualsItself() {
        val item = PaymentMethodSelectorItem(paymentMethods = emptyList(), currentSelected = PaymentMethod.CARD)
        assertTrue(item == item)
    }

    @Test
    @DisplayName("PaymentMethodSelectorItem hashCode equals for equal items")
    fun selectorItemHashCodeEquals() {
        val methods = listOf(PaymentMethod.CARD)
        val item1 = PaymentMethodSelectorItem(paymentMethods = methods, currentSelected = PaymentMethod.CARD)
        val item2 = PaymentMethodSelectorItem(paymentMethods = methods, currentSelected = PaymentMethod.CARD)
        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    @DisplayName("PaymentMethodItemAction enum values are all accessible")
    fun paymentMethodItemActionValues() {
        val values = PaymentMethodItemAction.values()
        assertTrue(values.contains(PaymentMethodItemAction.UNKNOWN))
        assertTrue(values.contains(PaymentMethodItemAction.EDIT))
        assertTrue(values.contains(PaymentMethodItemAction.DONE))
        assertTrue(values.contains(PaymentMethodItemAction.ADD_CARD))
        assertTrue(values.contains(PaymentMethodItemAction.PICK_CARD))
        assertTrue(values.contains(PaymentMethodItemAction.PICK_BANK))
        assertTrue(values.contains(PaymentMethodItemAction.EDIT_CARD))
        assertTrue(values.contains(PaymentMethodItemAction.DELETE_CARD))
        assertTrue(values.contains(PaymentMethodItemAction.SELECT_PAYMENT_METHOD))
    }

    @Test
    @DisplayName("PaymentMethodItemType enum has expected values")
    fun paymentMethodItemTypeValues() {
        val values = PaymentMethodItemType.values()
        assertTrue(values.isNotEmpty())
        assertTrue(values.contains(PaymentMethodItemType.SELECTOR))
        assertTrue(values.contains(PaymentMethodItemType.SAVED_CARDS_HEADER))
        assertTrue(values.contains(PaymentMethodItemType.SAVED_CARDS_ITEM))
        assertTrue(values.contains(PaymentMethodItemType.SAVED_CARDS_FOOTER))
        assertTrue(values.contains(PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER))
    }
}

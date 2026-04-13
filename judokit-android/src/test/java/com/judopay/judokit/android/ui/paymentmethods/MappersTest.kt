package com.judopay.judokit.android.ui.paymentmethods

import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.ui.editcard.CardPattern
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.sql.Date

@DisplayName("Testing payment methods Mappers")
internal class MappersTest {
    private val entity =
        TokenizedCardEntity(
            id = 1,
            token = "tok_123",
            title = "Visa",
            expireDate = "12/25",
            ending = "1111",
            network = CardNetwork.VISA,
            pattern = CardPattern.BLACK,
            cardholderName = "John Doe",
            timestamp = Date(0),
        )

    @Test
    @DisplayName("toPaymentCardViewModel maps entity fields to view model")
    fun entityToPaymentCardViewModelMapsFields() {
        val viewModel =
            entity.toPaymentCardViewModel(
                newTitle = "My Visa",
                selectedPattern = CardPattern.TWILIGHT_BLUE,
            )
        assertEquals(1, viewModel.id)
        assertEquals(CardNetwork.VISA, viewModel.cardNetwork)
        assertEquals("My Visa", viewModel.name)
        assertEquals("1111", viewModel.maskedNumber)
        assertEquals("12/25", viewModel.expireDate)
        assertEquals(CardPattern.TWILIGHT_BLUE, viewModel.pattern)
    }

    @Test
    @DisplayName("toPaymentMethodSavedCardItem maps entity fields correctly")
    fun entityToPaymentMethodSavedCardItemMapsFields() {
        val item = entity.toPaymentMethodSavedCardItem()
        assertEquals(1, item.id)
        assertEquals("tok_123", item.token)
        assertEquals("Visa", item.title)
        assertEquals("12/25", item.expireDate)
        assertEquals("1111", item.ending)
        assertEquals(CardNetwork.VISA, item.network)
        assertEquals(CardPattern.BLACK, item.pattern)
        assertEquals("John Doe", item.cardholderName)
    }

    @Test
    @DisplayName("PaymentMethodSavedCardItem.toPaymentCardViewModel maps fields correctly")
    fun savedCardItemToPaymentCardViewModelMapsFields() {
        val item =
            PaymentMethodSavedCardItem(
                id = 5,
                title = "Mastercard",
                network = CardNetwork.MASTERCARD,
                ending = "9999",
                token = "tok_456",
                expireDate = "01/26",
                pattern = CardPattern.TWILIGHT_BLUE,
                cardholderName = "Jane Doe",
            )
        val viewModel = item.toPaymentCardViewModel()
        assertEquals(5, viewModel.id)
        assertEquals(CardNetwork.MASTERCARD, viewModel.cardNetwork)
        assertEquals("Mastercard", viewModel.name)
        assertEquals("9999", viewModel.maskedNumber)
        assertEquals("01/26", viewModel.expireDate)
        assertEquals(CardPattern.TWILIGHT_BLUE, viewModel.pattern)
        assertEquals("Jane Doe", viewModel.cardholderName)
    }
}

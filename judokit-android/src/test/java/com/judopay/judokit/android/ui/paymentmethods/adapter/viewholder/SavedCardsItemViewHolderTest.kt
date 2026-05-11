package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.judopay.judokit.android.databinding.SavedCardItemBinding
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.ui.editcard.CardPattern
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Locale

@DisplayName("SavedCardsItemViewHolder")
internal class SavedCardsItemViewHolderTest {
    private lateinit var binding: SavedCardItemBinding
    private lateinit var root: ConstraintLayout
    private lateinit var removeCardIcon: ImageView
    private lateinit var arrowIcon: ImageView
    private lateinit var radioIconImageView: ImageView
    private lateinit var networkIconContainer: FrameLayout
    private lateinit var networkIconImageView: ImageView
    private lateinit var subTitle: TextView
    private lateinit var title: TextView
    private lateinit var networkIconContainerParams: ViewGroup.MarginLayoutParams
    private lateinit var viewHolder: SavedCardsItemViewHolder

    @BeforeEach
    fun setUp() {
        binding = mockk(relaxed = true)
        root = mockk(relaxed = true)
        removeCardIcon = mockk(relaxed = true)
        arrowIcon = mockk(relaxed = true)
        radioIconImageView = mockk(relaxed = true)
        networkIconContainer = mockk(relaxed = true)
        networkIconImageView = mockk(relaxed = true)
        subTitle = mockk(relaxed = true)
        title = mockk(relaxed = true)
        networkIconContainerParams = mockk(relaxed = true)

        every { binding.root } returns root
        every { networkIconContainer.layoutParams } returns networkIconContainerParams

        setBindingField("arrowIcon", arrowIcon)
        setBindingField("networkIconContainer", networkIconContainer)
        setBindingField("networkIconImageView", networkIconImageView)
        setBindingField("radioIconImageView", radioIconImageView)
        setBindingField("removeCardIcon", removeCardIcon)
        setBindingField("subTitle", subTitle)
        setBindingField("title", title)

        viewHolder = SavedCardsItemViewHolder(binding)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun setBindingField(
        name: String,
        value: Any,
    ) {
        val field = SavedCardItemBinding::class.java.getDeclaredField(name)
        field.isAccessible = true
        field.set(binding, value)
    }

    @Suppress("LongParameterList")
    private fun buildModel(
        isInEditMode: Boolean = false,
        isSelected: Boolean = false,
        expireDate: String = futureExpireDate(),
        network: CardNetwork = CardNetwork.VISA,
        ending: String = "1234",
        title: String = "Visa",
    ) = PaymentMethodSavedCardItem(
        id = 1,
        title = title,
        network = network,
        ending = ending,
        token = "tok_abc",
        expireDate = expireDate,
        cardholderName = "John Doe",
        isSelected = isSelected,
        isInEditMode = isInEditMode,
        pattern = CardPattern.BLACK,
    )

    private fun futureExpireDate(): String {
        val future = Calendar.getInstance()
        future.add(Calendar.YEAR, 2)
        val month = String.format(Locale.ROOT, "%02d", future.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", future.get(Calendar.YEAR) % 100)
        return "$month/$year"
    }

    private val pastExpireDate: String = "01/10"

    private fun soonExpireDate(): String {
        val soon = Calendar.getInstance()
        soon.add(Calendar.MONTH, 1)
        val month = String.format(Locale.ROOT, "%02d", soon.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", soon.get(Calendar.YEAR) % 100)
        return "$month/$year"
    }

    @Nested
    @DisplayName("when isInEditMode is true")
    inner class EditModeTests {
        @Test
        @DisplayName("removeCardIcon and arrowIcon are shown")
        fun editIconsAreVisible() {
            viewHolder.bind(buildModel(isInEditMode = true), null)
            verify { removeCardIcon.visibility = View.VISIBLE }
            verify { arrowIcon.visibility = View.VISIBLE }
        }

        @Test
        @DisplayName("radioIconImageView is hidden")
        fun radioIconIsGone() {
            viewHolder.bind(buildModel(isInEditMode = true), null)
            verify { radioIconImageView.visibility = View.GONE }
        }

        @Test
        @DisplayName("DELETE_CARD action is dispatched when removeCardIcon is clicked")
        fun deleteCardListenerFired() {
            val clickListenerSlot = slot<View.OnClickListener>()
            every { removeCardIcon.setOnClickListener(capture(clickListenerSlot)) } just runs
            val listener = mockk<PaymentMethodsAdapterListener>(relaxed = true)
            val model = buildModel(isInEditMode = true)
            viewHolder.bind(model, listener)
            clickListenerSlot.captured.onClick(removeCardIcon)
            verify { listener(PaymentMethodItemAction.DELETE_CARD, model) }
        }

        @Test
        @DisplayName("EDIT_CARD action is dispatched when the row is clicked")
        fun editCardListenerFired() {
            val clickListenerSlot = slot<View.OnClickListener>()
            every { root.setOnClickListener(capture(clickListenerSlot)) } just runs
            val listener = mockk<PaymentMethodsAdapterListener>(relaxed = true)
            val model = buildModel(isInEditMode = true)
            viewHolder.bind(model, listener)
            clickListenerSlot.captured.onClick(root)
            verify { listener(PaymentMethodItemAction.EDIT_CARD, model) }
        }

        @Test
        @DisplayName("null listener does not crash when removeCardIcon is clicked")
        fun nullListenerDoesNotCrashOnDelete() {
            val clickListenerSlot = slot<View.OnClickListener>()
            every { removeCardIcon.setOnClickListener(capture(clickListenerSlot)) } just runs
            viewHolder.bind(buildModel(isInEditMode = true), null)
            assertDoesNotThrow { clickListenerSlot.captured.onClick(removeCardIcon) }
        }

        @Test
        @DisplayName("null listener does not crash when the row is clicked")
        fun nullListenerDoesNotCrashOnRowClick() {
            val clickListenerSlot = slot<View.OnClickListener>()
            every { root.setOnClickListener(capture(clickListenerSlot)) } just runs
            viewHolder.bind(buildModel(isInEditMode = true), null)
            assertDoesNotThrow { clickListenerSlot.captured.onClick(root) }
        }
    }

    @Nested
    @DisplayName("when isInEditMode is false")
    inner class NormalModeTests {
        @Test
        @DisplayName("removeCardIcon and arrowIcon are hidden")
        fun editIconsAreGone() {
            viewHolder.bind(buildModel(isInEditMode = false), null)
            verify { removeCardIcon.visibility = View.GONE }
            verify { arrowIcon.visibility = View.GONE }
        }

        @Test
        @DisplayName("radioIconImageView is shown")
        fun radioIconIsVisible() {
            viewHolder.bind(buildModel(isInEditMode = false), null)
            verify { radioIconImageView.visibility = View.VISIBLE }
        }

        @Test
        @DisplayName("radioIconImageView tag is 'true' when card is selected")
        fun radioTagTrueWhenSelected() {
            viewHolder.bind(buildModel(isSelected = true), null)
            verify { radioIconImageView.tag = "true" }
        }

        @Test
        @DisplayName("radioIconImageView tag is 'false' when card is not selected")
        fun radioTagFalseWhenNotSelected() {
            viewHolder.bind(buildModel(isSelected = false), null)
            verify { radioIconImageView.tag = "false" }
        }

        @Test
        @DisplayName("PICK_CARD action is dispatched when the row is clicked")
        fun pickCardListenerFired() {
            val clickListenerSlot = slot<View.OnClickListener>()
            every { root.setOnClickListener(capture(clickListenerSlot)) } just runs
            val listener = mockk<PaymentMethodsAdapterListener>(relaxed = true)
            val model = buildModel()
            viewHolder.bind(model, listener)
            clickListenerSlot.captured.onClick(root)
            verify { listener(PaymentMethodItemAction.PICK_CARD, model) }
        }

        @Test
        @DisplayName("null listener does not crash when the row is clicked")
        fun nullListenerDoesNotCrash() {
            val clickListenerSlot = slot<View.OnClickListener>()
            every { root.setOnClickListener(capture(clickListenerSlot)) } just runs
            viewHolder.bind(buildModel(), null)
            assertDoesNotThrow { clickListenerSlot.captured.onClick(root) }
        }
    }

    @Nested
    @DisplayName("title and subtitle")
    inner class TitleSubtitleTests {
        @Test
        @DisplayName("title text is populated from model")
        fun titleTextIsSet() {
            viewHolder.bind(buildModel(title = "Mastercard"), null)
            verify { title.text = "Mastercard" }
        }

        @Test
        @DisplayName("subtitle text is set for a valid card")
        fun subtitleTextIsSet() {
            viewHolder.bind(buildModel(ending = "1234"), null)
            verify { subTitle.text = any() }
        }

        @Test
        @DisplayName("subtitle text is set for an expired card and color is updated")
        fun subtitleTextSetForExpiredCard() {
            viewHolder.bind(buildModel(expireDate = pastExpireDate, ending = "1234"), null)
            verify { subTitle.text = any() }
            verify { subTitle.setTextColor(any<Int>()) }
        }

        @Test
        @DisplayName("subtitle text is set for a card expiring soon and color is updated")
        fun subtitleTextSetForExpiringSoonCard() {
            viewHolder.bind(buildModel(expireDate = soonExpireDate(), ending = "1234"), null)
            verify { subTitle.text = any() }
            verify { subTitle.setTextColor(any<Int>()) }
        }
    }

    @Nested
    @DisplayName("network icon")
    inner class NetworkIconTests {
        @Test
        @DisplayName("setImageResource is called for a known network (VISA)")
        fun setImageResourceCalledForKnownNetwork() {
            viewHolder.bind(buildModel(network = CardNetwork.VISA), null)
            verify { networkIconImageView.setImageResource(any()) }
        }

        @Test
        @DisplayName("setImageDrawable(null) is called for an unknown network")
        fun setImageDrawableNullCalledForUnknownNetwork() {
            viewHolder.bind(buildModel(network = CardNetwork.OTHER), null)
            verify { networkIconImageView.setImageDrawable(null) }
        }

        @Test
        @DisplayName("setImageResource is called for MASTERCARD")
        fun setImageResourceCalledForMastercard() {
            viewHolder.bind(buildModel(network = CardNetwork.MASTERCARD), null)
            verify { networkIconImageView.setImageResource(any()) }
        }
    }
}

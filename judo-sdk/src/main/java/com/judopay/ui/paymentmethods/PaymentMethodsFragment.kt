package com.judopay.ui.paymentmethods

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judopay.R
import com.judopay.api.model.response.Receipt
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.judo
import com.judopay.model.CardNetwork
import com.judopay.model.PaymentMethod
import com.judopay.model.formatted
import com.judopay.ui.cardentry.CardEntryFragment
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.ui.paymentmethods.adapter.SwipeToDeleteCallback
import com.judopay.ui.paymentmethods.components.PaymentCallToActionViewModel
import com.judopay.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.ui.paymentmethods.model.*
import kotlinx.android.synthetic.main.payment_call_to_action_view.*
import kotlinx.android.synthetic.main.payment_methods_fragment.*

interface PaymentMethodModel {
    val type: PaymentMethod
    val items: List<PaymentMethodItem>
}

data class CardPaymentMethodModel(
        override val type: PaymentMethod = PaymentMethod.CARD,
        val selectedCard: PaymentMethodSavedCardsItem?,
        override val items: List<PaymentMethodItem>
) : PaymentMethodModel

data class GooglePayPaymentMethodModel(
        override val type: PaymentMethod = PaymentMethod.GOOGLE_PAY,
        override val items: List<PaymentMethodItem>
) : PaymentMethodModel

data class IdealPaymentMethodModel(
        override val type: PaymentMethod = PaymentMethod.IDEAL,
        override val items: List<PaymentMethodItem>
) : PaymentMethodModel

data class PaymentMethodsModel(
        val headerModel: PaymentMethodsHeaderViewModel,
        val currentPaymentMethod: PaymentMethodModel
)

class PaymentMethodsFragment : Fragment(), PaymentMethodsAdapterListener, CardEntryFragment.OnResultListener {

    private lateinit var viewModel: PaymentMethodsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.payment_methods_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener { requireActivity().onBackPressed() }

        payButton.setOnClickListener {

        }

        val swipeHandler = object : SwipeToDeleteCallback() {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as PaymentMethodsAdapter
                val item = adapter.items[viewHolder.adapterPosition]
                (item as? PaymentMethodSavedCardsItem)?.let {
                    onDeleteCardItem(it)
                }
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun payWith(card: PaymentMethodSavedCardsItem) {
        viewModel.payWithToken(judo, card.token, card.ending).observe(viewLifecycleOwner, Observer {
            Log.d("PaymentMethodsFragment", "payWith")
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentMethodsViewModel::class.java)

        viewModel.allCards.observe(viewLifecycleOwner, Observer { cards ->
            cardsListDidUpdate(cards)
        })
    }

    // handle callbacks from the recycler view elements
    override fun invoke(action: PaymentMethodItemAction, item: PaymentMethodItem) {
        when (item) {
            is PaymentMethodSelectorItem -> {
                // selector logic
//                onPaymentMethodDidChange(item.currentSelected)
            }
            is PaymentMethodSavedCardsItem -> {

            }
            is PaymentMethodGenericItem -> {
                if (action == PaymentMethodItemAction.ADD_CARD) onAddCard()
                if (action == PaymentMethodItemAction.EDIT) onEdit()
            }
        }
    }

    override fun onResult(fragment: CardEntryFragment, response: Receipt) {
        fragment.dismiss()

        response.cardDetails?.let {
            val scheme = it.scheme?.toUpperCase() ?: "VISA"
            val card = TokenizedCardEntity(
                    token = it.token ?: "",
                    title = "Card for shopping",
                    expireDate = it.formattedEndDate,
                    ending = it.lastFour ?: "",
                    network = CardNetwork.valueOf(scheme))

            viewModel.insert(card)
        }
    }

    private fun onDeleteCardItem(item: PaymentMethodSavedCardsItem) {
        val builder = MaterialAlertDialogBuilder(context)
                .setTitle("Delete Card?")
                .setMessage("Are you sure you want to delete this card from your wallet?")
                .setPositiveButton("Delete") { dialog, which ->
                    viewModel.deleteCardWithId(item.id)
                }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun onEdit() {
    }

    private fun onAddCard() {
        val fragment = CardEntryFragment(this)
        fragment.show(parentFragmentManager, "CardEntryFragment")
    }

    // TODO: Move the logic from below to the viewModel
    private fun buildModel(selectedMethod: PaymentMethod): PaymentMethodsModel {
        val cardModel: CardViewModel

        val recyclerViewData = mutableListOf<PaymentMethodItem>()
        val allMethods = judo.paymentMethods.toList()
        val cards = viewModel.allCards.value

        if (allMethods.size > 1) {
            recyclerViewData.add(PaymentMethodSelectorItem(PaymentMethodItemType.SELECTOR, allMethods, selectedMethod))
        }

        val method: PaymentMethodModel = when (selectedMethod) {
            PaymentMethod.CARD -> {
                var selectedCard: PaymentMethodSavedCardsItem? = null
                if (cards.isNullOrEmpty()) {
                    // placeholder
                    recyclerViewData.add(PaymentMethodGenericItem(PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER))
                    cardModel = NoPaymentMethodSelectedViewModel()
                } else {

                    recyclerViewData.add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_HEADER))

                    // cards
                    val cardItems = cards.map { it.toPaymentMethodSavedCardsItem() }
                    recyclerViewData.addAll(cardItems)

                    // footer
                    recyclerViewData.add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_FOOTER))

                    selectedCard = cardItems.first()
                    cardModel = PaymentCardViewModel(
                            cardNetwork = selectedCard.network,
                            name = selectedCard.title,
                            maskedNumber = selectedCard.ending,
                            expireDate = selectedCard.expireDate
                    )
                }
                CardPaymentMethodModel(selectedCard = selectedCard, items = recyclerViewData)
            }

            PaymentMethod.GOOGLE_PAY -> {
                cardModel = GooglePayCardViewModel()
                GooglePayPaymentMethodModel(items = recyclerViewData)
            }

            PaymentMethod.IDEAL -> {
                cardModel = IdealPaymentCardViewModel()
                IdealPaymentMethodModel(items = recyclerViewData)
            }

            PaymentMethod.AMAZON_PAY -> TODO()
        }

        val callToActionModel = PaymentCallToActionViewModel(
                amount = judo.amount.formatted,
                isButtonEnabled = cardModel is PaymentCardViewModel // TODO: temporary
        )

        val headerViewModel = PaymentMethodsHeaderViewModel(cardModel, callToActionModel)
        return PaymentMethodsModel(headerViewModel, method)
    }

    private fun onPaymentMethodDidChange(method: PaymentMethod) {
        val model = buildModel(method)
        updateWithModel(model)
    }

    private fun cardsListDidUpdate(cards: List<TokenizedCardEntity>) {
        onPaymentMethodDidChange(PaymentMethod.CARD)  // TODO: temporary
    }

    private fun updateWithModel(model: PaymentMethodsModel) {
        headerView.model = model.headerModel

        val adapter = recyclerView.adapter as? PaymentMethodsAdapter
                ?: PaymentMethodsAdapter(listener = this)

        recyclerView.adapter = adapter
        adapter.items = model.currentPaymentMethod.items
    }

}

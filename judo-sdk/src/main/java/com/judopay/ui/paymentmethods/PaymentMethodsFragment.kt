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
import com.judopay.judo
import com.judopay.model.CardNetwork
import com.judopay.model.PaymentMethod
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.ui.cardentry.CardEntryFragment
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.ui.paymentmethods.adapter.SwipeToDeleteCallback
import com.judopay.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.ui.paymentmethods.model.*
import kotlinx.android.synthetic.main.payment_call_to_action_view.*
import kotlinx.android.synthetic.main.payment_methods_fragment.*

class PaymentMethodsFragment : Fragment(), PaymentMethodsAdapterListener, CardEntryFragment.OnResultListener {

    private lateinit var viewModel: PaymentMethodsViewModel


    private var selectedCard: PaymentMethodSavedCardsItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.payment_methods_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        payButton.setOnClickListener {
            selectedCard?.let { payWith(it) }
        }
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
            updateCardsListWith(cards)
        })

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

    // handle callbacks from the recycler view elements
    override fun invoke(action: PaymentMethodItemAction, item: PaymentMethodItem) {
        when (item) {
            is PaymentMethodSelectorItem -> {
                // selector logic
                when (item.currentSelected) {
                    PaymentMethod.CARD -> {
                        headerView.model = PaymentMethodsHeaderViewModel(
                                cardModel = PaymentCardViewModel()
                        )
                    }

                    PaymentMethod.GOOGLE_PAY -> {
                        headerView.model = PaymentMethodsHeaderViewModel(
                                cardModel = GooglePayCardViewModel()
                        )

                    }

                    PaymentMethod.IDEAL -> {
                        headerView.model = PaymentMethodsHeaderViewModel(
                                cardModel = IdealPaymentCardViewModel()
                        )

                    }
                }

            }
            is PaymentMethodSavedCardsItem -> {
                selectedCard = item
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

    private fun updateCardsListWith(cards: List<TokenizedCardEntity>) {
        val data = mutableListOf<PaymentMethodItem>().apply {

            // header
            with(judo) {
                if (paymentMethods.size > 1) {
                    add(PaymentMethodSelectorItem(PaymentMethodItemType.SELECTOR, paymentMethods.toList(), paymentMethods.first()))
                }
            }

            if (cards.isNotEmpty()) {
                add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_HEADER))

                // cards
                val cardItems = cards.map { it.toPaymentMethodSavedCardsItem() }
                addAll(cardItems)

                // footer
                add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_FOOTER))

                selectedCard = cardItems.first()
                payButton.isEnabled = true
            } else {
                // placeholder
                add(PaymentMethodGenericItem(PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER))
                payButton.isEnabled = false
            }
        }

        val adapter = recyclerView.adapter as? PaymentMethodsAdapter
                ?: PaymentMethodsAdapter(listener = this)
        recyclerView.adapter = adapter
        adapter.items = data
    }
}

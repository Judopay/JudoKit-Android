package com.judopay.ui.paymentmethods

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.judopay.R
import com.judopay.judo
import com.judopay.model.CardNetwork
import com.judopay.persistence.entity.TokenizedCardEntity
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.ui.paymentmethods.model.*
import kotlinx.android.synthetic.main.payment_methods_fragment.*

class PaymentMethodsFragment : Fragment(), PaymentMethodsAdapterListener {

    private lateinit var viewModel: PaymentMethodsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.payment_methods_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentMethodsViewModel::class.java)

        viewModel.deleteAll()
        viewModel.allCards.observe(viewLifecycleOwner, Observer { cards ->
            updateCardsListWith(cards)
        })
    }

    // handle callbacks from the recycler view elements
    override fun invoke(action: PaymentMethodItemAction, item: PaymentMethodItem) {
        when (item) {
            is PaymentMethodSelectorItem -> {
                // selector logic
            }
            is PaymentMethodSavedCardsItem -> {

            }
            is PaymentMethodGenericItem -> {
                if (action == PaymentMethodItemAction.ADD_CARD) onAddCard()
                if (action == PaymentMethodItemAction.EDIT) onEdit()
            }
        }
    }

    private fun onEdit() {
        Log.d("PaymentMethodsFragment", "onEdit")
    }

    private fun onAddCard() {
        val card = TokenizedCardEntity(
                token = "",
                title = "",
                expireDate = "",
                maskedNumber = "",
                network = CardNetwork.AMEX)

        viewModel.insert(card)

//        CardEntryFragment().show(parentFragmentManager, "TES")
    }

    private fun updateCardsListWith(cards: List<TokenizedCardEntity>) {
        val data = mutableListOf<PaymentMethodItem>().apply {

            // header
            with(judo) {
                if (paymentMethods.size > 1) {
                    add(PaymentMethodSelectorItem(PaymentMethodItemType.SELECTOR, paymentMethods.toList(), paymentMethods.first()))
                }
            }

            add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_HEADER))

            // cards
            val cardItems = cards.map { it.toPaymentMethodSavedCardsItem() }
            addAll(cardItems)

            // footer
            add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_FOOTER))
        }

        val adapter = recyclerView.adapter as? PaymentMethodsAdapter
                ?: PaymentMethodsAdapter(listener = this)
        recyclerView.adapter = adapter
        adapter.items = data
    }
}

package com.judopay.ui.paymentmethods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.judopay.R
import com.judopay.ui.cardentry.CardEntryFragment
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.ui.paymentmethods.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.model.PaymentMethodItemType
import kotlinx.android.synthetic.main.payment_methods_fragment.*

class PaymentMethodsFragment : Fragment() {

    private lateinit var viewModel: PaymentMethodsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.payment_methods_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentMethodsViewModel::class.java)

        val data = arrayListOf(
                PaymentMethodItem(PaymentMethodItemType.SELECTOR),
                PaymentMethodItem(PaymentMethodItemType.SAVED_CARDS_HEADER),
                PaymentMethodItem(PaymentMethodItemType.SAVED_CARDS_ITEM),
                PaymentMethodItem(PaymentMethodItemType.SAVED_CARDS_ITEM),
                PaymentMethodItem(PaymentMethodItemType.SAVED_CARDS_ITEM),
                PaymentMethodItem(PaymentMethodItemType.SAVED_CARDS_FOOTER)
        )

        recyclerView.adapter = PaymentMethodsAdapter(data) { item, action ->
            when (action) {
                PaymentMethodItemAction.ADD_CARD -> onAddCard()
                else -> {
                    Snackbar.make(coordinatorLayout, "$action is unimplemented", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onAddCard() {
        // TBI - Add card

        CardEntryFragment().show(parentFragmentManager, "TES")
    }

}

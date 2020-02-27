package com.judopay.ui.paymentmethods

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.judopay.R
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.judo
import com.judopay.ui.cardentry.CardEntryFragment
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.ui.paymentmethods.model.PaymentMethodGenericItem
import com.judopay.ui.paymentmethods.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.model.PaymentMethodItemType
import com.judopay.ui.paymentmethods.model.PaymentMethodSavedCardsItem
import com.judopay.ui.paymentmethods.model.PaymentMethodSelectorItem
import kotlinx.android.synthetic.main.payment_methods_fragment.recyclerView
kotlinx.android.synthetic.main.payment_call_to_action_view.payButton

class PaymentMethodsFragment : Fragment() {

    private lateinit var viewModel: PaymentMethodsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.payment_methods_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val api = JudoApiServiceFactory.createApiService(requireContext(), judo);
        val factory = PaymentMethodsViewModel.PaymentMethodsViewModelFactory(api)
        viewModel = ViewModelProvider(this, factory).get(PaymentMethodsViewModel::class.java)
        payButton.setOnClickListener { viewModel.pay(judo) }

        // TODO: Extract this logic
        val data = arrayListOf(
                PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_HEADER),
                PaymentMethodSavedCardsItem(),
                PaymentMethodSavedCardsItem(),
                PaymentMethodSavedCardsItem(),
                PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_FOOTER)
        )

        with(judo) {
            if (paymentMethods.size > 1) {
                data.add(
                    0, PaymentMethodSelectorItem(
                        PaymentMethodItemType.SELECTOR,
                        paymentMethods.toList(),
                        paymentMethods.first()
                    )
                )
            }
        }

        recyclerView.adapter = PaymentMethodsAdapter(data) { item, action ->
            when (action) {
                PaymentMethodItemAction.ADD_CARD -> onAddCard()
                else -> {
                    Log.d("PaymentMethodsFragment", item.toString())
                }
            }
        }
    }

    private fun onAddCard() {
        CardEntryFragment().show(parentFragmentManager, "TES")
    }

}

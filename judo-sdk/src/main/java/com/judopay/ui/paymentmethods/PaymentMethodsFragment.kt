package com.judopay.ui.paymentmethods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judopay.JUDO_RECEIPT
import com.judopay.JudoPaymentResult
import com.judopay.JudoSharedViewModel
import com.judopay.R
import com.judopay.api.error.ApiError
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.judo
import com.judopay.ui.cardverification.CardVerificationFragment
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.ui.paymentmethods.adapter.SwipeToDeleteCallback
import com.judopay.ui.paymentmethods.adapter.model.*
import com.judopay.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.ui.paymentmethods.model.PaymentMethodModel
import kotlinx.android.synthetic.main.payment_methods_fragment.*
import kotlinx.android.synthetic.main.payment_methods_header_view.*

data class PaymentMethodsModel(
        val headerModel: PaymentMethodsHeaderViewModel,
        val currentPaymentMethod: PaymentMethodModel
)

class PaymentMethodsFragment : Fragment() {

    private lateinit var viewModel: PaymentMethodsViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.payment_methods_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButtonCallbacks()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val factory = PaymentMethodsViewModelFactory(application, judo)

        viewModel = ViewModelProvider(this, factory).get(PaymentMethodsViewModel::class.java)
        viewModel.model.observe(viewLifecycleOwner, Observer { updateWithModel(it) })

        viewModel.judoApiCallResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JudoApiCallResult.Success -> handleSuccess(it.data)
                is JudoApiCallResult.Failure -> handleFail(it.error)
            }
        })

        // TODO: to be refactored
        viewModel.allCardsSync.observe(viewLifecycleOwner, Observer {
            viewModel.send(PaymentMethodsAction.Update)
        })
    }

    private fun handleFail(error: ApiError?) {
        if (error != null) {
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(error))
        }
    }

    private fun handleSuccess(receipt: Receipt?) {
        if (receipt != null)
            if (receipt.is3dSecureRequired) {
                val bundle = Bundle().apply {
                    putParcelable(JUDO_RECEIPT, receipt)
                }
                val cardVerificationFragment = CardVerificationFragment().apply {
                    arguments = bundle
                }
                requireActivity().supportFragmentManager.beginTransaction().add(
                    R.id.container,
                    cardVerificationFragment
                ).commitNow()
            } else {
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Success(receipt))
            }
    }

    // handle callbacks from the recycler view elements
    private fun dispatchRecyclerViewAction(action: PaymentMethodItemAction,
                                           item: PaymentMethodItem) {
        when (item) {
            is PaymentMethodSelectorItem -> {
                // selector logic
            }
            is PaymentMethodSavedCardItem -> {
                viewModel.send(PaymentMethodsAction.SelectStoredCard(item.id))
            }
            is PaymentMethodGenericItem -> {
                if (action == PaymentMethodItemAction.ADD_CARD) onAddCard()
                if (action == PaymentMethodItemAction.EDIT) onEdit()
            }
        }
    }

    private fun onDeleteCardItem(item: PaymentMethodSavedCardItem) {
        MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_card_alert_title)
                .setMessage(R.string.delete_card_alert_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete) { _, _ ->
                    viewModel.send(PaymentMethodsAction.DeleteCard(item.id))
                }
                .show()
    }

    private fun onEdit() {}

    private fun onAddCard() = findNavController().navigate(R.id.action_paymentMethodsFragment_to_cardEntryFragment)

    private fun updateWithModel(model: PaymentMethodsModel) {
        headerView.model = model.headerModel

        val adapter = recyclerView.adapter as? PaymentMethodsAdapter
        adapter?.items = model.currentPaymentMethod.items
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = PaymentMethodsAdapter(listener = ::dispatchRecyclerViewAction)

        val swipeHandler = object : SwipeToDeleteCallback() {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as PaymentMethodsAdapter
                val item = adapter.items[viewHolder.adapterPosition]
                (item as? PaymentMethodSavedCardItem)?.let {
                    onDeleteCardItem(it)
                }
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupButtonCallbacks() {
        backButton.setOnClickListener(::onUserCancelled)

        paymentCallToActionView.callbackListener = {
            viewModel.send(PaymentMethodsAction.PayWithSelectedStoredCard)
        }
    }

    private fun onUserCancelled(view: View) {
        // disable the button
        view.isEnabled = false

        // post the event
        sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled)
    }
}

package com.judopay.ui.paymentmethods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judopay.JudoSharedAction
import com.judopay.JudoSharedViewModel
import com.judopay.R
import com.judopay.api.error.ApiError
import com.judopay.api.error.toJudoError
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.PbbaSaleResponse
import com.judopay.api.model.response.Receipt
import com.judopay.api.model.response.toCardVerificationModel
import com.judopay.api.model.response.toJudoResult
import com.judopay.api.polling.PollingResult
import com.judopay.judo
import com.judopay.model.JudoPaymentResult
import com.judopay.ui.editcard.JUDO_TOKENIZED_CARD_ID
import com.judopay.ui.ideal.JUDO_IDEAL_BANK
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.ui.paymentmethods.adapter.SwipeToDeleteCallback
import com.judopay.ui.paymentmethods.adapter.model.IdealBankItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.ui.paymentmethods.components.PaymentCallToActionType
import com.judopay.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.ui.paymentmethods.model.PaymentMethodModel
import com.zapp.library.merchant.ui.PBBAPopupCallback
import com.zapp.library.merchant.util.PBBAAppUtils
import kotlinx.android.synthetic.main.payment_methods_fragment.*
import kotlinx.android.synthetic.main.payment_methods_header_view.*

internal const val CARD_VERIFICATION = "com.judopay.model.CardVerificationModel"

data class PaymentMethodsModel(
    val headerModel: PaymentMethodsHeaderViewModel,
    val currentPaymentMethod: PaymentMethodModel
)

class PaymentMethodsFragment : Fragment() {

    private lateinit var viewModel: PaymentMethodsViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.payment_methods_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.fromEditMode = true
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

        viewModel.payWithIdealObserver.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { bic ->
                findNavController().navigate(
                    R.id.action_paymentMethodsFragment_to_idealFragment, bundleOf(
                        JUDO_IDEAL_BANK to bic
                    )
                )
            }
        })

        viewModel.payByBankResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JudoApiCallResult.Success ->
                    handlePbbaSaleResponse(it.data)
                is JudoApiCallResult.Failure -> {
                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.transaction_error_title)
                        .setMessage(R.string.transaction_unsuccessful)
                        .setNegativeButton(R.string.close, null)
                        .show()
                }
            }
        })

        viewModel.payByBankStatusResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PollingResult.Processing -> {
                    pollingStatusView.processing { viewModel.cancelPbbaPayment() }
                }
                is PollingResult.Delay -> {
                    pollingStatusView.delay {
                        viewModel.resetPbbaPolling()
                        pollingStatusView.processing { viewModel.cancelPbbaPayment() }
                    }
                }
                is PollingResult.Retry -> {
                    pollingStatusView.retry {
                        viewModel.retryPbbaPolling()
                        pollingStatusView.processing { viewModel.cancelPbbaPayment() }
                    }
                }
                is PollingResult.Failure -> {
                    pollingStatusView.fail { requireActivity().finish() }
                    if (it.error != null) {
                        sharedViewModel.paymentResult.postValue(
                            JudoPaymentResult.Error(it.error.toJudoError())
                        )
                    }
                }
                is PollingResult.Success -> {
                    pollingStatusView.success { requireActivity().finish() }
                    if (it.data != null) {
                        val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
                        sharedViewModel.paymentResult.postValue(
                            JudoPaymentResult.Success(it.data.toJudoResult(locale))
                        )
                    }
                }
            }
        })

        sharedViewModel.paymentMethodsGooglePayResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                viewModel.send(PaymentMethodsAction.UpdatePayWithGooglePayButtonState(true))
                sharedViewModel.paymentResult.postValue(result)
            })
    }

    private fun handlePbbaSaleResponse(data: PbbaSaleResponse?) {
        if (data != null) {
            PBBAAppUtils.showPBBAPopup(
                requireActivity(),
                data.secureToken,
                data.pbbaBrn,
                object : PBBAPopupCallback {
                    override fun onRetryPaymentRequest() {
                        // noop
                    }

                    override fun onDismissPopup() {
                        // noop
                    }
                }
            )
        }
    }

    private fun handleFail(error: ApiError?) {
        if (error != null) {
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(error.toJudoError()))
        }
    }

    private fun handleSuccess(receipt: Receipt?) {
        if (receipt != null)
            if (receipt.is3dSecureRequired) {
                findNavController().navigate(
                    R.id.action_paymentMethodsFragment_to_cardVerificationFragment, bundleOf(
                        CARD_VERIFICATION to receipt.toCardVerificationModel()
                    )
                )
            } else {
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Success(receipt.toJudoResult()))
            }
    }

    // handle callbacks from the recycler view elements
    private fun dispatchRecyclerViewAction(
        action: PaymentMethodItemAction,
        item: PaymentMethodItem
    ) {
        when (item) {
            is PaymentMethodSelectorItem -> {
                viewModel.send(
                    PaymentMethodsAction.SelectPaymentMethod(
                        item.currentSelected
                    )
                )
            }
            is PaymentMethodSavedCardItem -> {
                if (action == PaymentMethodItemAction.EDIT_CARD) {
                    onEdit(item.id)
                    viewModel.send(PaymentMethodsAction.SelectStoredCard(item.id))
                }
                if (action == PaymentMethodItemAction.DELETE_CARD)
                    onDeleteCardItem(item)
                if (action == PaymentMethodItemAction.PICK_CARD)
                    viewModel.send(PaymentMethodsAction.SelectStoredCard(item.id))
            }
            is PaymentMethodGenericItem -> {
                if (action == PaymentMethodItemAction.ADD_CARD)
                    onAddCard()
                if (action == PaymentMethodItemAction.EDIT)
                    viewModel.send(PaymentMethodsAction.EditMode(true))
                if (action == PaymentMethodItemAction.DONE)
                    viewModel.send(PaymentMethodsAction.EditMode(false))
            }
            is IdealBankItem -> viewModel.send(PaymentMethodsAction.SelectIdealBank(item.idealBank))
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

    private fun onEdit(cardId: Int) {
        findNavController().navigate(
            R.id.action_paymentMethodsFragment_to_editCardFragment,
            bundleOf(JUDO_TOKENIZED_CARD_ID to cardId)
        )
    }

    private fun onAddCard() =
        findNavController().navigate(R.id.action_paymentMethodsFragment_to_cardEntryFragment)

    private fun updateWithModel(model: PaymentMethodsModel) {
        headerView.paymentMethods = judo.paymentMethods.toList()
        headerView.model = model.headerModel

        val adapter = recyclerView.adapter as? PaymentMethodsAdapter
        adapter?.items = model.currentPaymentMethod.items
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = PaymentMethodsAdapter(
            listener = ::dispatchRecyclerViewAction
        )

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
            when (it) {
                PaymentCallToActionType.PAY_WITH_CARD ->
                    viewModel.send(PaymentMethodsAction.PayWithSelectedStoredCard)

                PaymentCallToActionType.PAY_WITH_GOOGLE_PAY -> {
                    sharedViewModel.send(JudoSharedAction.LoadGPayPaymentData)
                    viewModel.send(PaymentMethodsAction.UpdatePayWithGooglePayButtonState(false))
                }

                PaymentCallToActionType.PAY_WITH_IDEAL ->
                    viewModel.send(PaymentMethodsAction.PayWithSelectedIdealBank)

                PaymentCallToActionType.PAY_WITH_PAY_BY_BANK ->
                    viewModel.send(PaymentMethodsAction.PayWithPayByBank)
            }
        }
    }

    private fun onUserCancelled(view: View) {
        // disable the button
        view.isEnabled = false

        // post the event
        sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled())
    }
}

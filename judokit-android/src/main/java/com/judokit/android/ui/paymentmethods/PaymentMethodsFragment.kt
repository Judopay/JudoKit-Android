package com.judokit.android.ui.paymentmethods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judokit.android.JudoSharedAction
import com.judokit.android.JudoSharedViewModel
import com.judokit.android.R
import com.judokit.android.api.error.ApiError
import com.judokit.android.api.error.toJudoError
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.api.model.response.BankSaleResponse
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.CardDate
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.api.model.response.toCardVerificationModel
import com.judokit.android.api.model.response.toJudoResult
import com.judokit.android.db.JudoRoomDatabase
import com.judokit.android.db.repository.TokenizedCardRepository
import com.judokit.android.judo
import com.judokit.android.model.JudoError
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.JudoResult
import com.judokit.android.service.polling.PollingResult
import com.judokit.android.service.polling.PollingService
import com.judokit.android.ui.common.getLocale
import com.judokit.android.ui.editcard.JUDO_TOKENIZED_CARD_ID
import com.judokit.android.ui.ideal.JUDO_IDEAL_BANK
import com.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judokit.android.ui.paymentmethods.adapter.SwipeToDeleteCallback
import com.judokit.android.ui.paymentmethods.adapter.model.IdealBankItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judokit.android.ui.paymentmethods.components.PaymentCallToActionType
import com.judokit.android.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewAction
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState
import com.judokit.android.ui.paymentmethods.model.PaymentMethodModel
import com.zapp.library.merchant.ui.PBBAPopupCallback
import com.zapp.library.merchant.util.PBBAAppUtils
import kotlinx.android.synthetic.main.payment_methods_fragment.*
import kotlinx.android.synthetic.main.payment_methods_header_view.*

internal const val CARD_VERIFICATION = "com.judokit.android.model.CardVerificationModel"

data class PaymentMethodsModel(
    val headerModel: PaymentMethodsHeaderViewModel,
    val currentPaymentMethod: PaymentMethodModel
)

class PaymentMethodsFragment : Fragment() {

    private lateinit var viewModel: PaymentMethodsViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var bankOrderId: String? = null

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
        val cardDate = CardDate()
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        val cardRepository = TokenizedCardRepository(tokenizedCardDao)
        val service = JudoApiServiceFactory.createApiService(application, judo)
        val pollingService = PollingService(service)
        val factory =
            PaymentMethodsViewModelFactory(cardDate, cardRepository, service, pollingService, application, judo)

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
                    handleBankSaleResponse(it.data)
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
            handleBankResult(it)
        })

        sharedViewModel.paymentMethodsGooglePayResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                viewModel.send(PaymentMethodsAction.UpdatePayWithGooglePayButtonState(true))
                sharedViewModel.paymentResult.postValue(result)
            })

        pollingStatusView.onButtonClickListener = { handlePollingStatusViewButtonClick(it) }
    }

    private fun handlePollingStatusViewButtonClick(action: PollingStatusViewAction) =
        when (action) {
            PollingStatusViewAction.RETRY -> {
                when (pollingStatusView.state) {
                    PollingStatusViewState.DELAY -> viewModel.send(PaymentMethodsAction.ResetBankPolling)
                    PollingStatusViewState.RETRY -> viewModel.send(PaymentMethodsAction.RetryBankPolling)
                    else -> {
                        // noop
                    }
                }

                pollingStatusView.state = PollingStatusViewState.PROCESSING
            }

            PollingStatusViewAction.CLOSE -> {
                when (pollingStatusView.state) {
                    PollingStatusViewState.FAIL,
                    PollingStatusViewState.SUCCESS -> requireActivity().finish()
                    else -> viewModel.send(PaymentMethodsAction.CancelBankPayment)
                }
            }
        }

    private fun handleBankResult(pollingResult: PollingResult<BankSaleStatusResponse>?) {
        pollingStatusView.state = when (pollingResult) {
            is PollingResult.Processing -> PollingStatusViewState.PROCESSING
            is PollingResult.Delay -> PollingStatusViewState.DELAY
            is PollingResult.Retry -> PollingStatusViewState.RETRY
            is PollingResult.Success -> PollingStatusViewState.SUCCESS
            is PollingResult.Failure -> PollingStatusViewState.FAIL
            else -> null
        }

        when (pollingResult) {
            is PollingResult.Failure -> {
                val error = pollingResult.error?.toJudoError() ?: JudoError.generic()
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(error))
            }
            is PollingResult.Success -> {
                val result = pollingResult.data?.toJudoResult(getLocale(resources)) ?: JudoResult()
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Success(result))
            }
        }
    }

    private fun handleBankSaleResponse(data: BankSaleResponse?) {
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
            bankOrderId = data.orderId
        } else {
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(JudoError.generic()))
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

    override fun onResume() {
        super.onResume()
        if (!bankOrderId.isNullOrEmpty()) {
            viewModel.send(PaymentMethodsAction.StartBankPayment(bankOrderId!!))
            bankOrderId = null
        }
    }
}

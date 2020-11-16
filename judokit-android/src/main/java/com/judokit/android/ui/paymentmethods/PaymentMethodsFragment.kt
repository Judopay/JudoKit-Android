package com.judokit.android.ui.paymentmethods

import android.net.Uri
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
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.error.ApiError
import com.judokit.android.api.error.toJudoError
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.api.model.response.CardDate
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.api.model.response.toCardVerificationModel
import com.judokit.android.api.model.response.toJudoResult
import com.judokit.android.db.JudoRoomDatabase
import com.judokit.android.db.repository.TokenizedCardRepository
import com.judokit.android.judo
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.ui.cardverification.THREE_DS_ONE_DIALOG_FRAGMENT_TAG
import com.judokit.android.ui.cardverification.ThreeDSOneCardVerificationDialogFragment
import com.judokit.android.ui.cardverification.ThreeDSOneCompletionCallback
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
import com.judokit.android.ui.paymentmethods.model.PaymentMethodModel
import kotlinx.android.synthetic.main.payment_methods_fragment.*
import kotlinx.android.synthetic.main.payment_methods_header_view.*

internal const val PAYMENT_WIDGET_TYPE = "com.judokit.android.model.paymentWidgetType"
internal const val CARD_NETWORK = "com.judokit.android.cardNetwork"

data class PaymentMethodsModel(
    val headerModel: PaymentMethodsHeaderViewModel,
    val currentPaymentMethod: PaymentMethodModel
)

class PaymentMethodsFragment : Fragment() {

    private lateinit var viewModel: PaymentMethodsViewModel
    private lateinit var service: JudoApiService
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

        judo.pbbaConfiguration?.deepLinkURL?.let {
            if (it != Uri.EMPTY) {
                navigateToPollingStatus()
            }
        }
        val application = requireActivity().application
        val cardDate = CardDate()
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        val cardRepository = TokenizedCardRepository(tokenizedCardDao)
        service = JudoApiServiceFactory.createApiService(application, judo)
        val factory =
            PaymentMethodsViewModelFactory(cardDate, cardRepository, service, application, judo)

        viewModel = ViewModelProvider(this, factory).get(PaymentMethodsViewModel::class.java)
        viewModel.model.observe(viewLifecycleOwner, Observer { updateWithModel(it) })

        viewModel.judoApiCallResult.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is JudoApiCallResult.Success -> handleSuccess(it.data)
                    is JudoApiCallResult.Failure -> handleFail(it.error)
                }
            }
        )

        // TODO: to be refactored
        viewModel.allCardsSync.observe(
            viewLifecycleOwner,
            Observer {
                viewModel.send(PaymentMethodsAction.Update)
            }
        )

        viewModel.payWithIdealObserver.observe(
            viewLifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let { bic ->
                    findNavController().navigate(
                        R.id.action_paymentMethodsFragment_to_idealFragment,
                        bundleOf(
                            JUDO_IDEAL_BANK to bic
                        )
                    )
                }
            }
        )

        viewModel.payWithPayByBankObserver.observe(
            viewLifecycleOwner,
            Observer {
                if (!it.hasBeenHandled()) {
                    navigateToPollingStatus()
                }
            }
        )

        viewModel.selectedCardNetworkObserver.observe(
            viewLifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let { cardNetwork ->
                    findNavController().navigate(
                        R.id.action_paymentMethodsFragment_to_cardEntryFragment,
                        bundleOf(
                            CARD_NETWORK to cardNetwork
                        )
                    )
                }
            }
        )

        sharedViewModel.paymentMethodsResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                viewModel.send(PaymentMethodsAction.UpdateButtonState(true))
                sharedViewModel.paymentResult.postValue(result)
            }
        )
        sharedViewModel.securityCodeResult.observe(
            viewLifecycleOwner,
            Observer { securityCode ->
                viewModel.send(PaymentMethodsAction.PayWithSelectedStoredCard(securityCode))
            }
        )
    }

    private fun navigateToPollingStatus() {
        findNavController().navigate(
            R.id.action_paymentMethodsFragment_to_PollingStatusFragment,
            bundleOf(
                PAYMENT_WIDGET_TYPE to PaymentWidgetType.PAY_BY_BANK_APP
            )
        )
    }

    private fun handleFail(error: ApiError?) {
        if (error != null) {
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(error.toJudoError()))
        }
    }

    private fun handleSuccess(receipt: Receipt?) {
        if (receipt != null)
            if (receipt.is3dSecureRequired) {

                val callback = object :
                    ThreeDSOneCompletionCallback {
                    override fun onSuccess(success: JudoPaymentResult) {
                        sharedViewModel.paymentResult.postValue((success))
                    }

                    override fun onFailure(error: JudoPaymentResult) {
                        sharedViewModel.paymentResult.postValue((error))
                    }
                }

                ThreeDSOneCardVerificationDialogFragment(
                    service,
                    receipt.toCardVerificationModel(),
                    callback
                ).show(childFragmentManager, THREE_DS_ONE_DIALOG_FRAGMENT_TAG)
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
        MaterialAlertDialogBuilder(requireContext())
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
                PaymentCallToActionType.PAY_WITH_CARD -> {
                    viewModel.send(PaymentMethodsAction.PayWithSelectedStoredCard())
                }
                PaymentCallToActionType.PAY_WITH_GOOGLE_PAY -> {
                    sharedViewModel.send(JudoSharedAction.LoadGPayPaymentData)
                    viewModel.send(PaymentMethodsAction.UpdateButtonState(false))
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

package com.judopay.judokit.android.ui.paymentmethods

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judopay.judokit.android.JudoSharedAction
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.db.JudoRoomDatabase
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.service.CardTransactionService
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.editcard.JUDO_TOKENIZED_CARD_ID
import com.judopay.judokit.android.ui.ideal.JUDO_IDEAL_BANK
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.judokit.android.ui.paymentmethods.adapter.SwipeToDeleteCallback
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.IdealBankItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentCallToActionType
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentMethodModel
import kotlinx.android.synthetic.main.payment_methods_fragment.*
import kotlinx.android.synthetic.main.payment_methods_header_view.*

internal const val PAYMENT_WIDGET_TYPE = "com.judopay.judokit.android.model.paymentWidgetType"
internal const val CARD_ENTRY_OPTIONS = "com.judopay.judokit.android.cardEntryOptions"

data class PaymentMethodsModel(
    val headerModel: PaymentMethodsHeaderViewModel,
    val currentPaymentMethod: PaymentMethodModel
)

class PaymentMethodsFragment : Fragment() {

    private lateinit var viewModel: PaymentMethodsViewModel
    private lateinit var service: JudoApiService
    private lateinit var cardTransactionService: CardTransactionService
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
        cardTransactionService = CardTransactionService(requireActivity(), judo, service)

        val factory =
            PaymentMethodsViewModelFactory(
                cardDate,
                cardRepository,
                cardTransactionService,
                application,
                judo
            )

        viewModel = ViewModelProvider(this, factory).get(PaymentMethodsViewModel::class.java)
        viewModel.model.observe(viewLifecycleOwner, { updateWithModel(it) })

        viewModel.judoPaymentResult.observe(
            viewLifecycleOwner,
            { sharedViewModel.paymentResult.postValue((it)) }
        )

        // TODO: to be refactored
        viewModel.allCardsSync.observe(
            viewLifecycleOwner,
            {
                viewModel.send(PaymentMethodsAction.Update)
            }
        )

        viewModel.payWithIdealObserver.observe(
            viewLifecycleOwner,
            {
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
            {
                if (!it.hasBeenHandled()) {
                    navigateToPollingStatus()
                }
            }
        )

        viewModel.displayCardEntryObserver.observe(
            viewLifecycleOwner,
            {
                it.getContentIfNotHandled()?.let { cardEntryOptions ->
                    findNavController().navigate(
                        R.id.action_paymentMethodsFragment_to_cardEntryFragment,
                        bundleOf(
                            CARD_ENTRY_OPTIONS to cardEntryOptions
                        )
                    )
                }
            }
        )

        sharedViewModel.paymentMethodsResult.observe(
            viewLifecycleOwner,
            { result ->
                viewModel.send(PaymentMethodsAction.UpdateButtonState(true))
                sharedViewModel.paymentResult.postValue(result)
            }
        )
        sharedViewModel.cardEntryToPaymentMethodResult.observe(
            viewLifecycleOwner,
            { transactionDetailBuilder ->
                viewModel.send(PaymentMethodsAction.PayWithCard(transactionDetailBuilder))
            }
        )
    }

    override fun onDestroy() {
        cardTransactionService.close()
        super.onDestroy()
    }

    private fun navigateToPollingStatus() {
        findNavController().navigate(
            R.id.action_paymentMethodsFragment_to_PollingStatusFragment,
            bundleOf(
                PAYMENT_WIDGET_TYPE to PaymentWidgetType.PAY_BY_BANK_APP
            )
        )
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
        findNavController().navigate(
            R.id.action_paymentMethodsFragment_to_cardEntryFragment,
            bundleOf(
                CARD_ENTRY_OPTIONS to CardEntryOptions(
                    fromPaymentMethods = true,
                    shouldDisplayBillingDetails = false,
                    shouldDisplaySecurityCode = null,
                    addCardPressed = true
                )
            )
        )

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
                    viewModel.send(PaymentMethodsAction.InitiateSelectedCardPayment)
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

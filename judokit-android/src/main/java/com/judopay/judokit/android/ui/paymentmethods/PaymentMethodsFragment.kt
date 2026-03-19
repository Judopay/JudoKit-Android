package com.judopay.judokit.android.ui.paymentmethods

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.transaction.challenge.ChallengeStatusReceiver
import com.judopay.judokit.android.JudoSharedAction
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.animateWithAlpha
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.applyHorizontalCutoutPadding
import com.judopay.judokit.android.cardRepository
import com.judopay.judokit.android.databinding.PaymentMethodsFragmentBinding
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.THREE_DS_TWO_MIN_TIMEOUT
import com.judopay.judokit.android.service.ThreeDSSDKChallengeStatus
import com.judopay.judokit.android.service.toFormattedEventString
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.ANIMATION_DURATION_150
import com.judopay.judokit.android.ui.common.LANDSCAPE_COLLAPSE_THRESHOLD
import com.judopay.judokit.android.ui.common.viewModelFactory
import com.judopay.judokit.android.ui.editcard.JUDO_TOKENIZED_CARD_ID
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapter
import com.judopay.judokit.android.ui.paymentmethods.adapter.SwipeToDeleteCallback
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentCallToActionType
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentMethodModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

internal const val CARD_ENTRY_OPTIONS = "com.judopay.judokit.android.cardEntryOptions"
internal const val USER_CANCELLED = "com.judopay.judokit.android.userCancelled"

data class PaymentMethodsModel(
    val headerModel: PaymentMethodsHeaderViewModel,
    val currentPaymentMethod: PaymentMethodModel,
)

class PaymentMethodsFragment : Fragment() {
    private lateinit var viewModel: PaymentMethodsViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var viewBinding: PaymentMethodsFragmentBinding? = null
    private val binding get() = viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = PaymentMethodsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerView.fromEditMode = true
        setupWindowInsetsListeners()
        setupRecyclerView()
        setupButtonCallbacks()
        setupLandscapeCollapseListener()
        initializeViewModelObserving()
    }

    private fun initializeViewModel() {
        val cardTransactionRepository = CardTransactionRepository.create(requireContext(), judo)
        val factory =
            viewModelFactory {
                PaymentMethodsViewModel(CardDate(), cardRepository(), cardTransactionRepository, requireActivity().application, judo)
            }
        viewModel = ViewModelProvider(this, factory)[PaymentMethodsViewModel::class.java]
    }

    private fun initializeViewModelObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.filterNotNull().collect { updateWithModel(it) } }
                launch { viewModel.paymentResultEffect.collect { sharedViewModel.postPaymentResult(it) } }
                launch {
                    viewModel.cardEntryEffect.collect { cardEntryOptions ->
                        findNavController().navigate(
                            R.id.action_paymentMethodsFragment_to_cardEntryFragment,
                            bundleOf(CARD_ENTRY_OPTIONS to cardEntryOptions),
                        )
                    }
                }
                launch {
                    sharedViewModel.paymentMethodsResultEffect.collect { result ->
                        viewModel.send(PaymentMethodsAction.UpdateButtonState(true))
                        sharedViewModel.postPaymentResult(result)
                    }
                }
                launch {
                    sharedViewModel.cardEntryToPaymentMethodResultEffect.collect { transactionDetailBuilder ->
                        viewModel.send(PaymentMethodsAction.PayWithCard(transactionDetailBuilder))
                    }
                }
                launch {
                    findNavController()
                        .currentBackStackEntry
                        ?.savedStateHandle
                        ?.getStateFlow(USER_CANCELLED, false)
                        ?.filter { it }
                        ?.collect { viewModel.send(PaymentMethodsAction.UpdateButtonState(true)) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pendingChallenge.filterNotNull().collect { data ->
                data.transaction.doChallenge(
                    requireActivity(),
                    data.challengeParameters,
                    object : ChallengeStatusReceiver {
                        override fun completed(event: CompletionEvent) = viewModel.onChallengeResult(event.toFormattedEventString())

                        override fun cancelled() = viewModel.onChallengeResult(ThreeDSSDKChallengeStatus.CANCELLED)

                        override fun protocolError(event: ProtocolErrorEvent) = viewModel.onChallengeResult(event.toFormattedEventString())

                        override fun runtimeError(event: RuntimeErrorEvent) = viewModel.onChallengeResult(event.toFormattedEventString())

                        override fun timedout() = viewModel.onChallengeResult(ThreeDSSDKChallengeStatus.TIMEOUT)
                    },
                    THREE_DS_TWO_MIN_TIMEOUT,
                )
            }
        }
    }

    // handle callbacks from the recycler view elements
    private fun dispatchRecyclerViewAction(
        action: PaymentMethodItemAction,
        item: PaymentMethodItem,
    ) {
        when (item) {
            is PaymentMethodSelectorItem -> {
                viewModel.send(
                    PaymentMethodsAction.SelectPaymentMethod(
                        item.currentSelected,
                    ),
                )
            }
            is PaymentMethodSavedCardItem -> {
                if (action == PaymentMethodItemAction.EDIT_CARD) {
                    onEdit(item.id)
                    viewModel.send(PaymentMethodsAction.SelectStoredCard(item.id))
                }
                if (action == PaymentMethodItemAction.DELETE_CARD) {
                    onDeleteCardItem(item)
                }
                if (action == PaymentMethodItemAction.PICK_CARD) {
                    viewModel.send(PaymentMethodsAction.SelectStoredCard(item.id))
                }
            }
            is PaymentMethodGenericItem -> {
                if (action == PaymentMethodItemAction.ADD_CARD) {
                    onAddCard()
                }
                if (action == PaymentMethodItemAction.EDIT) {
                    viewModel.send(PaymentMethodsAction.EditMode(true))
                }
                if (action == PaymentMethodItemAction.DONE) {
                    viewModel.send(PaymentMethodsAction.EditMode(false))
                }
            }
        }
    }

    private fun onDeleteCardItem(item: PaymentMethodSavedCardItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.jp_delete_card_alert_title)
            .setMessage(R.string.jp_delete_card_alert_message)
            .setNegativeButton(R.string.jp_cancel, null)
            .setPositiveButton(R.string.jp_delete) { _, _ ->
                viewModel.send(PaymentMethodsAction.DeleteCard(item.id))
            }.show()
    }

    private fun onEdit(cardId: Int) {
        findNavController().navigate(
            R.id.action_paymentMethodsFragment_to_editCardFragment,
            bundleOf(JUDO_TOKENIZED_CARD_ID to cardId),
        )
    }

    private fun onAddCard() =
        findNavController().navigate(
            R.id.action_paymentMethodsFragment_to_cardEntryFragment,
            bundleOf(
                CARD_ENTRY_OPTIONS to
                    CardEntryOptions(
                        isPresentedFromPaymentMethods = true,
                        isAddingNewCard = true,
                    ),
            ),
        )

    private fun updateWithModel(model: PaymentMethodsModel) {
        binding.headerView.paymentMethods = judo.paymentMethods.toList()
        binding.headerView.model = model.headerModel

        val adapter = binding.recyclerView.adapter as? PaymentMethodsAdapter
        adapter?.items = model.currentPaymentMethod.items
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter =
            PaymentMethodsAdapter(
                listener = ::dispatchRecyclerViewAction,
            )

        val swipeHandler =
            object : SwipeToDeleteCallback() {
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int,
                ) {
                    val adapter = binding.recyclerView.adapter as PaymentMethodsAdapter
                    val item = adapter.items[viewHolder.adapterPosition]
                    (item as? PaymentMethodSavedCardItem)?.let {
                        onDeleteCardItem(it)
                    }
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                }
            }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupWindowInsetsListeners() {
        // Passes insets down for nested views
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, insets ->
            val platformInsets = insets.toWindowInsets()
            binding.headerView.dispatchApplyWindowInsets(platformInsets)
            binding.toolbar.dispatchApplyWindowInsets(platformInsets)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { view, insets ->
            view.applyHorizontalCutoutPadding(insets)
            view.updatePadding(
                bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + resources.getDimension(R.dimen.space_48).toInt(),
            )
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            view.applyHorizontalCutoutPadding(insets)
            insets
        }
    }

    private fun setupButtonCallbacks() {
        binding.backButton.setOnClickListener(::onUserCancelled)

        binding.headerView.binding.paymentCallToActionView.callbackListener = {
            when (it) {
                PaymentCallToActionType.PAY_WITH_CARD -> {
                    viewModel.send(PaymentMethodsAction.InitiateSelectedCardPayment)
                }
                PaymentCallToActionType.PAY_WITH_GOOGLE_PAY -> {
                    sharedViewModel.send(JudoSharedAction.LoadGPayPaymentData)
                    viewModel.send(PaymentMethodsAction.UpdateButtonState(false))
                }
            }
        }
    }

    private fun setupLandscapeCollapseListener() {
        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) return

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (totalScrollRange == 0) return@addOnOffsetChangedListener

            val isCollapsed = -verticalOffset.toFloat() / totalScrollRange > LANDSCAPE_COLLAPSE_THRESHOLD
            val headerBinding = binding.headerView.binding
            val viewsToHide = listOf(headerBinding.noPaymentMethodSelectedView, headerBinding.viewAnimator)

            viewsToHide.forEach { view ->
                val alpha = if (isCollapsed) 0f else 1f
                view.animateWithAlpha(alpha, ANIMATION_DURATION_150)
            }
        }
    }

    private fun onUserCancelled(view: View) {
        // disable the button
        view.isEnabled = false

        // post the event
        sharedViewModel.postPaymentResult(JudoPaymentResult.UserCancelled())
    }
}

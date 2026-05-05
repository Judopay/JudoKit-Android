package com.judopay.judokit.android.ui.cardentry

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.transaction.challenge.ChallengeStatusReceiver
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.cardRepository
import com.judopay.judokit.android.databinding.CardEntryFragmentBinding
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.initAutofillAndAccessibilityOnAttach
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.isCardPaymentWidget
import com.judopay.judokit.android.model.isPaymentMethodsWidget
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.THREE_DS_TWO_MIN_TIMEOUT
import com.judopay.judokit.android.service.ThreeDSSDKChallengeStatus
import com.judopay.judokit.android.service.toFormattedEventString
import com.judopay.judokit.android.ui.cardentry.components.BillingDetailsFormListener
import com.judopay.judokit.android.ui.cardentry.components.CardEntryFormListener
import com.judopay.judokit.android.ui.cardentry.components.JudoBottomSheetDialog
import com.judopay.judokit.android.ui.cardentry.components.adjustContainerLayoutMargins
import com.judopay.judokit.android.ui.cardentry.components.updateAppBarsOnScrollChange
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.common.parcelable
import com.judopay.judokit.android.ui.common.viewModelFactory
import com.judopay.judokit.android.ui.paymentmethods.CARD_ENTRY_OPTIONS
import com.judopay.judokit.android.ui.paymentmethods.USER_CANCELLED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

private const val BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME = 300L
private const val BOTTOM_SHEET_EXPAND_ANIMATION_TIME = BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME / 6
private const val BOTTOM_SHEET_PEEK_HEIGHT = 200
private const val KEYBOARD_DISMISS_TIMEOUT = 500L
private const val KEY_DISPLAYED_CHILD = "key_displayed_child"

@Suppress("DEPRECATION")
class CardEntryFragment : BottomSheetDialogFragment() {
    private val viewModel: CardEntryViewModel by viewModels {
        val cardEntryOptions = arguments?.parcelable<CardEntryOptions>(CARD_ENTRY_OPTIONS) ?: CardEntryOptions()
        viewModelFactory {
            CardEntryViewModel(
                judo,
                CardTransactionRepository.create(requireContext(), judo),
                cardRepository(),
                cardEntryOptions,
                requireActivity().application,
            )
        }
    }
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var viewBinding: CardEntryFragmentBinding? = null
    private val binding get() = viewBinding!!

    override fun getTheme(): Int = R.style.JudoTheme_BottomSheetDialogTheme

    // present it always expanded
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        JudoBottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = BOTTOM_SHEET_PEEK_HEIGHT
            isCancelable = false
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = CardEntryFragmentBinding.inflate(inflater, container, false)
        return binding.root
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

        initializeViewModelObserving()
        viewModel.send(CardEntryAction.Initialize)

        setupScrollElevation()
        setupWindowInsets()
        setupDisplayCutoutInsets()
        setupContainerMargins()

        // Restore which form was visible before a configuration change.
        savedInstanceState?.let {
            val child = it.getInt(KEY_DISPLAYED_CHILD, 0)
            if (child != 0) binding.cardEntryViewAnimator.showBilling()
        }

        binding.cancelButton.setOnClickListener { onUserCancelled() }
        binding.cardDetailsFormView.listener = cardEntryFormListener()
        binding.billingAddressFormView.listener = billingDetailsFormListener()
        binding.cardEntryViewAnimator.initAutofillAndAccessibilityOnAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewBinding?.let {
            outState.putInt(KEY_DISPLAYED_CHILD, it.cardEntryViewAnimator.displayedChild)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onUserCancelled()
    }

    private fun cardEntryFormListener() =
        object : CardEntryFormListener {
            override fun onFieldChanged(
                type: CardDetailsFieldType,
                value: String,
                event: FormFieldEvent,
            ) {
                viewModel.send(CardEntryAction.CardFieldChanged(type, value, event))
            }

            override fun onSubmit() {
                binding.root.dismissKeyboard()
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(KEYBOARD_DISMISS_TIMEOUT)
                    viewModel.send(CardEntryAction.SubmitCardEntryForm)
                }
            }
        }

    private fun billingDetailsFormListener() =
        object : BillingDetailsFormListener {
            override fun onFieldChanged(
                type: BillingDetailsFieldType,
                value: String,
                event: FormFieldEvent,
            ) {
                viewModel.send(CardEntryAction.BillingFieldChanged(type, value, event))
            }

            override fun onSubmit() {
                viewModel.send(CardEntryAction.SubmitBillingDetailsForm)
            }

            override fun onBack() {
                viewModel.send(CardEntryAction.PressBackButton)
            }
        }

    private fun setupScrollElevation() {
        val topAppBar = binding.cardEntryToolbar
        val billingView = binding.billingAddressFormView
        val cardView = binding.cardDetailsFormView

        binding.cardEntryViewAnimator.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateAppBarsOnScrollChange(topAppBar, billingView.bottomAppBar, billingView.scrollView)
            updateAppBarsOnScrollChange(topAppBar, cardView.bottomAppBar, cardView.scrollView)
        }

        billingView.scrollView.viewTreeObserver.addOnScrollChangedListener {
            updateAppBarsOnScrollChange(topAppBar, billingView.bottomAppBar, billingView.scrollView)
        }

        cardView.scrollView.viewTreeObserver.addOnScrollChangedListener {
            updateAppBarsOnScrollChange(topAppBar, cardView.bottomAppBar, cardView.scrollView)
        }
    }

    private fun setupWindowInsets() {
        val insetsListener =
            OnApplyWindowInsetsListener { view, insets ->
                view.setPadding(view.paddingLeft, 0, view.paddingRight, 0)
                insets
            }

        ViewCompat.setOnApplyWindowInsetsListener(binding.billingAddressFormView.bottomAppBar, insetsListener)
        ViewCompat.setOnApplyWindowInsetsListener(binding.cardDetailsFormView.bottomAppBar, insetsListener)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(view.paddingLeft, 0, view.paddingRight, maxOf(navigationBarInsets.bottom, imeInsets.bottom))
            insets
        }
    }

    private fun setupDisplayCutoutInsets() {
        val cutoutInsetsListener =
            OnApplyWindowInsetsListener { view, insets ->
                val cutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
                view.setPadding(
                    maxOf(view.paddingLeft, cutoutInsets.left),
                    0,
                    maxOf(view.paddingRight, cutoutInsets.right),
                    0,
                )
                insets
            }

        ViewCompat.setOnApplyWindowInsetsListener(binding.cardEntryViewAnimator, cutoutInsetsListener)
        ViewCompat.setOnApplyWindowInsetsListener(binding.cardEntryToolbar, cutoutInsetsListener)
    }

    private fun setupContainerMargins() {
        val topAppBar = binding.cardEntryToolbar
        val billingView = binding.billingAddressFormView
        val cardView = binding.cardDetailsFormView

        adjustContainerLayoutMargins(billingView.containerLayout, topAppBar, billingView.bottomAppBar)
        adjustContainerLayoutMargins(cardView.containerLayout, topAppBar, cardView.bottomAppBar)
    }

    private fun initializeViewModelObserving() {
        // The 3DS2 challenge runs on top of JudoActivity, which drops to STOPPED during the challenge.
        // Using pendingChallenge (StateFlow) instead of a one-shot SharedFlow means a Fragment
        // recreated during a configuration change immediately re-sees any in-progress challenge and
        // re-calls doChallenge with the fresh Activity. The receiver calls viewModel.onChallengeResult
        // directly, so it survives even if this coroutine is later cancelled.
        // repeatOnLifecycle(STARTED) is safe here because StateFlow replays its current value when
        // STARTED is re-entered, so a recreated Fragment will immediately receive any pending challenge.
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.pendingChallenge.filterNotNull().collect { data ->
                        data.transaction.doChallenge(
                            requireActivity(),
                            data.challengeParameters,
                            object : ChallengeStatusReceiver {
                                override fun completed(event: CompletionEvent) = viewModel.onChallengeResult(event.toFormattedEventString())

                                override fun cancelled() = viewModel.onChallengeResult(ThreeDSSDKChallengeStatus.CANCELLED)

                                override fun protocolError(event: ProtocolErrorEvent) =
                                    viewModel.onChallengeResult(event.toFormattedEventString())

                                override fun runtimeError(event: RuntimeErrorEvent) =
                                    viewModel.onChallengeResult(event.toFormattedEventString())

                                override fun timedout() = viewModel.onChallengeResult(ThreeDSSDKChallengeStatus.TIMEOUT)
                            },
                            THREE_DS_TWO_MIN_TIMEOUT,
                        )
                    }
                }
                launch { viewModel.uiState.filterNotNull().collect { updateWithModel(it) } }
                launch { viewModel.paymentResultEffect.collect { dispatchResult(it) } }
                launch {
                    viewModel.cardEntryToPaymentMethodResultEffect.collect {
                        sharedViewModel.postCardEntryToPaymentMethodResult(it)
                        findNavController().popBackStack()
                    }
                }
                launch { viewModel.navigationEffect.collect { onHandleFormNavigation(it) } }
            }
        }
    }

    private fun onHandleFormNavigation(navigationEvent: CardEntryNavigation) {
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        viewLifecycleOwner.lifecycleScope.launch {
            delay(BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME)
            with(binding.cardEntryViewAnimator) {
                when (navigationEvent) {
                    is CardEntryNavigation.Card -> showCard()
                    is CardEntryNavigation.Billing -> showBilling()
                }
                postDelayed(BOTTOM_SHEET_EXPAND_ANIMATION_TIME) {
                    bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    private fun onUserCancelled() {
        binding.cancelButton.isEnabled = false
        findNavController().previousBackStackEntry?.savedStateHandle?.set(USER_CANCELLED, true)
        if (judo.paymentWidgetType.isPaymentMethodsWidget) {
            findNavController().popBackStack()
        } else {
            sharedViewModel.postPaymentResult(JudoPaymentResult.UserCancelled())
        }
    }

    private fun updateWithModel(model: CardEntryFragmentModel) {
        val formModel = model.formModel

        binding.apply {
            bottomSheetContainer.isVisible = model.isUserInputRequired
            cardDetailsFormView.model = formModel.cardDetailsInputModel
            billingAddressFormView.model = formModel.billingDetailsInputModel
        }
    }

    private fun dispatchResult(result: JudoPaymentResult) {
        when {
            judo.paymentWidgetType.isPaymentMethodsWidget ->
                when (result) {
                    is JudoPaymentResult.Success -> persistTokenizedCard(result)
                    is JudoPaymentResult.Error -> sharedViewModel.postPaymentResult(result)
                    is JudoPaymentResult.UserCancelled -> onUserCancelled()
                }
            judo.paymentWidgetType.isCardPaymentWidget ->
                when (result) {
                    is JudoPaymentResult.Success -> sharedViewModel.postPaymentResult(result)
                    is JudoPaymentResult.Error ->
                        sharedViewModel.postPaymentResult(
                            JudoPaymentResult.Error(result.error),
                        )
                    is JudoPaymentResult.UserCancelled -> onUserCancelled()
                }
            else -> sharedViewModel.postPaymentResult(result)
        }
    }

    private fun persistTokenizedCard(result: JudoPaymentResult.Success) {
        val cardDetails = result.result.cardDetails
        if (cardDetails != null) {
            viewModel.send(CardEntryAction.InsertCard(cardDetails))
            findNavController().popBackStack()
        } else {
            sharedViewModel.postPaymentResult(result)
        }
    }

    private val bottomSheetDialog: JudoBottomSheetDialog
        get() = dialog as JudoBottomSheetDialog
}

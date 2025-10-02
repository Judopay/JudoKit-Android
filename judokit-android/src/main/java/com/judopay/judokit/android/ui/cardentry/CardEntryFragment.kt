package com.judopay.judokit.android.ui.cardentry

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.view.autofill.AutofillManager
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.CardEntryFragmentBinding
import com.judopay.judokit.android.db.JudoRoomDatabase
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.isCardPaymentWidget
import com.judopay.judokit.android.model.isPaymentMethodsWidget
import com.judopay.judokit.android.service.CardTransactionManager
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.heightWithInsetsAndMargins
import com.judopay.judokit.android.ui.common.parcelable
import com.judopay.judokit.android.ui.paymentmethods.CARD_ENTRY_OPTIONS

private const val BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME = 300L
private const val BOTTOM_SHEET_PEEK_HEIGHT = 200
private const val KEYBOARD_DISMISS_TIMEOUT = 500L
private const val BOTTOM_APP_BAR_ELEVATION_CHANGE_DURATION = 200L
private const val CARD_ENTRY_FADE_ANIMATION_DURATION = 300L
private const val CARD_ENTRY_FADE_ANIMATION_VISIBILITY_POINT = 0.3f

class JudoBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
) : BottomSheetDialog(context, theme) {
    // Used to store the dialog window parameters.
    private var token: IBinder? = null

    private val isDialogResizedWorkaroundRequired: Boolean
        get() {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O || Build.VERSION.SDK_INT != Build.VERSION_CODES.O_MR1) {
                return false
            }
            val autofillManager =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getSystemService(AutofillManager::class.java)
                } else {
                    null
                }
            return autofillManager?.isEnabled ?: false
        }

    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams) {
        if (params.token == null && token != null) {
            params.token = token
        }

        super.onWindowAttributesChanged(params)
    }

    override fun onAttachedToWindow() {
        if (isDialogResizedWorkaroundRequired) {
            token = ownerActivity!!.window.attributes.token
        }

        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnShowListener {
            val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)

            behavior.addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {
                    var keyboardDismissed = false

                    override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float,
                    ) {
                        if (slideOffset < 0 && !keyboardDismissed) {
                            bottomSheet.dismissKeyboard()
                            keyboardDismissed = true
                        }
                    }

                    override fun onStateChanged(
                        bottomSheet: View,
                        newState: Int,
                    ) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            keyboardDismissed = false
                        }
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED ||
                            newState == BottomSheetBehavior.STATE_HIDDEN
                        ) {
                            bottomSheet.dismissKeyboard()
                        }
                    }
                },
            )
        }
    }
}

class CardEntryFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: CardEntryViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var _binding: CardEntryFragmentBinding? = null
    private val binding get() = _binding!!
    private var isCardEntryViewVisible = true

    override fun getTheme(): Int = R.style.JudoTheme_BottomSheetDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

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
        _binding = CardEntryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("LongMethod")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewModelObserving()
        binding.cancelButton.setOnClickListener { onUserCancelled() }

        binding.cardDetailsFormView.apply {
            onFormValidationStatusListener = { model, isValid ->
                viewModel.send(
                    CardEntryAction.ValidationStatusChanged(
                        model,
                        isValid,
                    ),
                )
            }
            onCardEntryButtonClickListener = {
                dismissKeyboard()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        viewModel.send(CardEntryAction.SubmitCardEntryForm)
                    },
                    KEYBOARD_DISMISS_TIMEOUT,
                )
            }
        }

        binding.billingAddressFormView.apply {
            onFormValidationStatusListener = { model, isValid ->
                viewModel.send(
                    CardEntryAction.BillingDetailsValidationStatusChanged(
                        model,
                        isValid,
                    ),
                )
            }
            onBillingDetailsBackButtonClickListener =
                { viewModel.send(CardEntryAction.PressBackButton) }
            onBillingDetailsSubmitButtonClickListener =
                { viewModel.send(CardEntryAction.SubmitBillingDetailsForm) }
        }

        // sets bottomAppBar elevation based on scroll
        val billingBinding = binding.billingAddressFormView.binding
        billingBinding.billingDetailsScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
                val autoTransition = AutoTransition()
                autoTransition.duration = BOTTOM_APP_BAR_ELEVATION_CHANGE_DURATION
                TransitionManager.beginDelayedTransition(billingBinding.billingContainer, autoTransition)
                if (v.canScrollVertically(1)) {
                    billingBinding.billingDetailsBottomAppBar.elevation = resources.getDimension(R.dimen.elevation_4)
                } else {
                    billingBinding.billingDetailsBottomAppBar.elevation = resources.getDimension(R.dimen.elevation_0)
                }
            },
        )

        // Prevents bottom sheet dialog to jump around when animating.
        val transition = LayoutTransition()
        transition.setAnimateParentHierarchy(false)
        binding.bottomSheetContainer.layoutTransition = transition

        // Handle spacing to ensure content appears right below toolbar
        updateToolbarAndContentSpacing()

        // Set up bottom sheet slide callback for fade animations
        bottomSheetDialog.behavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // Fade out content when sliding down (collapsing), fade in when sliding up (expanding)
                    val shouldBeVisible = slideOffset >= CARD_ENTRY_FADE_ANIMATION_VISIBILITY_POINT
                    if (shouldBeVisible != isCardEntryViewVisible) {
                        isCardEntryViewVisible = shouldBeVisible
                        animateCardEntryViewVisibility(shouldBeVisible)
                    }
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {}
            }
        )
    }

    override fun onStart() {
        super.onStart()
        subscribeToInsetsChanges()
        viewModel.send(CardEntryAction.SubscribeToCardTransactionManagerResults)
    }

    override fun onStop() {
        unSubscribeFromInsetsChanges()
        super.onStop()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onUserCancelled()
    }

    override fun onDestroy() {
        viewModel.send(CardEntryAction.UnSubscribeToCardTransactionManagerResults)
        super.onDestroy()
    }

    private fun initializeViewModel() {
        val application =
            requireActivity().application
        val tokenizedCardDao =
            JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        val cardRepository =
            TokenizedCardRepository(tokenizedCardDao)
        val cardEntryOptions =
            arguments?.parcelable<CardEntryOptions>(CARD_ENTRY_OPTIONS)
        val cardTransactionManager =
            CardTransactionManager.getInstance(requireActivity())
        cardTransactionManager.configureWith(judo)
        val factory =
            CardEntryViewModelFactory(
                judo,
                cardTransactionManager,
                cardRepository,
                cardEntryOptions,
                application,
            )
        viewModel =
            ViewModelProvider(this, factory)[CardEntryViewModel::class.java]
    }

    private fun initializeViewModelObserving() {
        viewModel.model.observe(viewLifecycleOwner) { updateWithModel(it) }
        viewModel.judoPaymentResult.observe(viewLifecycleOwner) { dispatchResult(it) }
        viewModel.cardEntryToPaymentMethodResult.observe(
            viewLifecycleOwner,
        ) {
            sharedViewModel.cardEntryToPaymentMethodResult.postValue(it)
            findNavController().popBackStack()
        }

        viewModel.navigationObserver.observe(
            viewLifecycleOwner,
        ) {
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    when (it) {
                        is CardEntryNavigation.Card -> binding.cardEntryViewAnimator.displayedChild = 0
                        is CardEntryNavigation.Billing -> binding.cardEntryViewAnimator.displayedChild = 1
                    }
                    bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                },
                BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME,
            )
        }
    }

    private fun onUserCancelled() {
        // disable the button
        binding.cancelButton.isEnabled = false
        findNavController().previousBackStackEntry?.savedStateHandle?.set("user-cancelled", true)
        if (judo.paymentWidgetType.isPaymentMethodsWidget) {
            findNavController().popBackStack()
        } else {
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled())
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
        if (judo.paymentWidgetType.isPaymentMethodsWidget) {
            // we're pushed on top of the payment methods fragment
            // we need to persist the card and that should trigger an update in the payment methods screen
            dispatchPaymentMethodsApiResult(result)
            return
        }
        if (judo.paymentWidgetType.isCardPaymentWidget) {
            dispatchCardPaymentApiResult(result)
            return
        }

        // in any other cases we're the only fragment in the stack,
        // so push the result to the parent activity
        sharedViewModel.paymentResult.postValue(result)
    }

    private fun dispatchCardPaymentApiResult(result: JudoPaymentResult) {
        when (result) {
            is JudoPaymentResult.Success -> sharedViewModel.paymentResult.postValue(result)
            is JudoPaymentResult.Error ->
                sharedViewModel.paymentResult.postValue(
                    JudoPaymentResult.Error(
                        result.error,
                    ),
                )
            is JudoPaymentResult.UserCancelled -> onUserCancelled()
        }
    }

    private fun dispatchPaymentMethodsApiResult(result: JudoPaymentResult) {
        when (result) {
            is JudoPaymentResult.Success -> persistTokenizedCard(result)
            is JudoPaymentResult.Error -> sharedViewModel.paymentResult.postValue(result)
            is JudoPaymentResult.UserCancelled -> onUserCancelled()
        }
    }

    private fun persistTokenizedCard(result: JudoPaymentResult.Success) {
        val cardDetails = result.result.cardDetails
        if (cardDetails != null) {
            viewModel.send(CardEntryAction.InsertCard(cardDetails))
            findNavController().popBackStack()
        } else {
            sharedViewModel.paymentResult.postValue(result)
        }
    }

    private fun unSubscribeFromInsetsChanges() =
        requireDialog().window?.apply {
            decorView.setOnApplyWindowInsetsListener(null)
        }

    // Animating view position based on the keyboard show/hide state
    private fun subscribeToInsetsChanges() =
        requireDialog().window?.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            val insetsListener =
                View.OnApplyWindowInsetsListener { view, windowInsets ->
                    // Update toolbar and content spacing when insets change
                    updateToolbarAndContentSpacing()
                    return@OnApplyWindowInsetsListener view.onApplyWindowInsets(windowInsets)
                }
            decorView.setOnApplyWindowInsetsListener(insetsListener)
        }

    private val bottomSheetDialog: JudoBottomSheetDialog
        get() = dialog as JudoBottomSheetDialog

    private fun updateToolbarAndContentSpacing() {
        val params = binding.cardEntryViewAnimator.layoutParams as MarginLayoutParams
        params.topMargin = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
             binding.cardEntryToolbar.heightWithInsetsAndMargins
        } else {
            (binding.cardEntryToolbar.height * 0.5).toInt()
        }
        binding.cardEntryToolbar.post {
            binding.cardEntryViewAnimator.layoutParams = params
        }
    }

    private fun animateCardEntryViewVisibility(visible: Boolean) {
        val targetAlpha = if (visible) 1.0f else 0.0f
        val animator = ObjectAnimator.ofFloat(binding.cardEntryViewAnimator, "alpha", targetAlpha)
        animator.duration = CARD_ENTRY_FADE_ANIMATION_DURATION
        animator.start()
    }
}

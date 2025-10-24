package com.judopay.judokit.android.ui.cardentry

import android.animation.LayoutTransition
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.view.autofill.AutofillManager
import android.widget.FrameLayout
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
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
            val bottomSheet = findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
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

            val cornerSize = context.resources.getDimension(R.dimen.size_16dp)
            val shapeAppearanceModel =
                ShapeAppearanceModel.builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                    .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                    .build()

            val backgroundDrawable =
                MaterialShapeDrawable(shapeAppearanceModel).apply {
                    fillColor = ColorStateList.valueOf(Color.WHITE)
                }

            bottomSheet.apply {
                clipChildren = true
                clipToPadding = true
                clipToOutline = true
                background = backgroundDrawable
            }
        }
    }
}

private fun updateAppBarsOnScrollChange(
    topAppBar: AppBarLayout,
    bottomAppBar: BottomAppBar,
    scrollView: NestedScrollView,
) {
    bottomAppBar.elevation =
        if (ViewCompat.canScrollVertically(scrollView, 1)) {
            scrollView.resources.getDimension(R.dimen.elevation_4)
        } else {
            scrollView.resources.getDimension(R.dimen.elevation_0)
        }

    topAppBar.outlineProvider =
        if (ViewCompat.canScrollVertically(scrollView, -1)) {
            ViewOutlineProvider.PADDED_BOUNDS
        } else {
            null
        }
}

fun waitForAllLaidOut(
    vararg views: View,
    onAllLaidOut: () -> Unit,
) {
    var remaining = views.size
    views.forEach { view ->
        view.doOnLayout {
            remaining--
            if (remaining == 0) {
                onAllLaidOut()
            }
        }
    }
}

private fun adjustContainerLayoutMargins(
    container: ConstraintLayout,
    topAppBar: AppBarLayout,
    bottomAppBar: BottomAppBar,
) {
    waitForAllLaidOut(container, topAppBar, bottomAppBar) {
        container.post {
            val params = container.layoutParams as MarginLayoutParams
            params.bottomMargin = bottomAppBar.heightWithInsetsAndMargins
            params.topMargin = topAppBar.heightWithInsetsAndMargins

            container.layoutParams = params
        }
    }
}

class CardEntryFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: CardEntryViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var _binding: CardEntryFragmentBinding? = null
    private val binding get() = _binding!!

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

        // sets top and bottom AppBar elevation based on scroll state
        val topAppBar = binding.cardEntryToolbar
        val billingBinding = binding.billingAddressFormView.binding
        val cardDetailsBinding = binding.cardDetailsFormView.binding

        binding.cardEntryViewAnimator.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateAppBarsOnScrollChange(topAppBar, billingBinding.billingDetailsBottomAppBar, billingBinding.billingDetailsScrollView)
            updateAppBarsOnScrollChange(topAppBar, cardDetailsBinding.cardEntryBottomAppBar, cardDetailsBinding.formScrollView)
        }

        billingBinding.billingDetailsScrollView.viewTreeObserver.addOnScrollChangedListener {
            updateAppBarsOnScrollChange(topAppBar, billingBinding.billingDetailsBottomAppBar, billingBinding.billingDetailsScrollView)
        }

        cardDetailsBinding.formScrollView.viewTreeObserver.addOnScrollChangedListener {
            updateAppBarsOnScrollChange(topAppBar, cardDetailsBinding.cardEntryBottomAppBar, cardDetailsBinding.formScrollView)
        }

        val insetsListener =
            OnApplyWindowInsetsListener { view, windowInsets ->
                view.setPadding(view.paddingLeft, 0, view.paddingRight, 0)
                WindowInsetsCompat.CONSUMED
            }

        ViewCompat.setOnApplyWindowInsetsListener(billingBinding.billingDetailsBottomAppBar, insetsListener)
        ViewCompat.setOnApplyWindowInsetsListener(cardDetailsBinding.cardEntryBottomAppBar, insetsListener)

        adjustContainerLayoutMargins(
            billingBinding.billingDetailsContainerLayout,
            binding.cardEntryToolbar,
            billingBinding.billingDetailsBottomAppBar,
        )
        adjustContainerLayoutMargins(
            cardDetailsBinding.cardDetailsContainerLayout,
            binding.cardEntryToolbar,
            cardDetailsBinding.cardEntryBottomAppBar,
        )

        // Prevents bottom sheet dialog to jump around when animating.
        val transition = LayoutTransition()
        transition.setAnimateParentHierarchy(false)
        binding.bottomSheetContainer.layoutTransition = transition

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val cutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            val modifiedStatusBarInsets =
                Insets.of(statusBarInsets.left, 0, statusBarInsets.right, statusBarInsets.bottom)
            val modifiedCutoutInsets =
                Insets.of(cutoutInsets.left, 0, cutoutInsets.right, cutoutInsets.bottom)

            val modifiedInsets =
                WindowInsetsCompat.Builder(insets)
                    .setInsets(WindowInsetsCompat.Type.statusBars(), modifiedStatusBarInsets)
                    .setInsets(WindowInsetsCompat.Type.displayCutout(), modifiedCutoutInsets)
                    .setInsets(WindowInsetsCompat.Type.navigationBars(), navigationBarInsets)
                    .setInsets(WindowInsetsCompat.Type.ime(), imeInsets)
                    .build()

            view.setPadding(view.paddingLeft, 0, view.paddingRight, maxOf(navigationBarInsets.bottom, imeInsets.bottom))

            modifiedInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.cardEntryViewAnimator) { view, insets ->
            view.setPadding(view.paddingLeft, 0, view.paddingRight, 0)

            insets
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.send(CardEntryAction.SubscribeToCardTransactionManagerResults)
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
        viewModel.navigationObserver.observe(viewLifecycleOwner) { onHandleFormNavigation(it) }
    }

    private fun onHandleFormNavigation(navigationEvent: CardEntryNavigation) {
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        Handler(Looper.getMainLooper()).postDelayed(
            {
                with(binding.cardEntryViewAnimator) {
                    displayedChild =
                        when (navigationEvent) {
                            is CardEntryNavigation.Card -> 0
                            is CardEntryNavigation.Billing -> 1
                        }
                    postDelayed(BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME / 6) {
                        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            },
            BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME,
        )
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

    private val bottomSheetDialog: JudoBottomSheetDialog
        get() = dialog as JudoBottomSheetDialog
}

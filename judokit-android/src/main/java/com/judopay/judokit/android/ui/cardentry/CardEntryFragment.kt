package com.judopay.judokit.android.ui.cardentry

import android.animation.LayoutTransition
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.SCAN_CARD_REQUEST_CODE
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.db.JudoRoomDatabase
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.isCardPaymentWidget
import com.judopay.judokit.android.model.isPaymentMethodsWidget
import com.judopay.judokit.android.service.CardTransactionService
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCompletionCallback
import com.judopay.judokit.android.ui.paymentmethods.CARD_ENTRY_OPTIONS
import kotlinx.android.synthetic.main.billing_details_form_view.*
import kotlinx.android.synthetic.main.card_entry_form_view.*
import kotlinx.android.synthetic.main.card_entry_fragment.*
import kotlinx.android.synthetic.main.card_entry_fragment.view.*
import kotlinx.android.synthetic.main.payment_methods_header_view.*
import kotlinx.android.synthetic.main.payment_methods_header_view.view.*

private const val BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME = 300L
private const val BOTTOM_SHEET_PEEK_HEIGHT = 200
private const val KEYBOARD_DISMISS_TIMEOUT = 500L
private const val BOTTOM_APP_BAR_ELEVATION_CHANGE_DURATION = 200L

class CardEntryFragment : BottomSheetDialogFragment(), ThreeDSOneCompletionCallback {

    private lateinit var viewModel: CardEntryViewModel
    private lateinit var service: JudoApiService
    private lateinit var cardTransactionService: CardTransactionService
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun getTheme(): Int = R.style.JudoTheme_BottomSheetDialogTheme

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        val cardRepository = TokenizedCardRepository(tokenizedCardDao)
        service = JudoApiServiceFactory.createApiService(application, judo)
        val cardEntryOptions = arguments?.getParcelable<CardEntryOptions>(CARD_ENTRY_OPTIONS)

        cardTransactionService =
            CardTransactionService(requireActivity(), judo, service)
        val factory = CardEntryViewModelFactory(
            judo,
            cardTransactionService,
            cardRepository,
            cardEntryOptions,
            application
        )

        viewModel = ViewModelProvider(this, factory).get(CardEntryViewModel::class.java)

        if (cardEntryOptions?.shouldDisplaySecurityCode != null) {
            viewModel.send(CardEntryAction.EnableFormFields(listOf(CardDetailsFieldType.SECURITY_NUMBER)))
        }

        viewModel.model.observe(viewLifecycleOwner, { updateWithModel(it) })
        viewModel.judoPaymentResult.observe(viewLifecycleOwner, { dispatchResult(it) })
        viewModel.cardEntryToPaymentMethodResult.observe(
            viewLifecycleOwner,
            {
                sharedViewModel.cardEntryToPaymentMethodResult.postValue(it)
                findNavController().popBackStack()
            }
        )

        viewModel.navigationObserver.observe(
            viewLifecycleOwner,
            {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        when (it) {
                            is CardEntryNavigation.Card -> cardEntryViewAnimator.displayedChild = 0
                            is CardEntryNavigation.Billing -> cardEntryViewAnimator.displayedChild = 1
                        }
                        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    },
                    BOTTOM_SHEET_COLLAPSE_ANIMATION_TIME
                )
            }
        )

        sharedViewModel.scanCardResult.observe(
            viewLifecycleOwner,
            {
                viewModel.send(CardEntryAction.ScanCard(it))
            }
        )
    }

    // present it always expanded
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = BOTTOM_SHEET_PEEK_HEIGHT
            isCancelable = false
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.card_entry_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelButton.setOnClickListener { onUserCancelled() }
        scanCardButton.setOnClickListener { handleScanCardButtonClicks() }
        formView.apply {
            onFormValidationStatusListener = { model, isValid ->
                viewModel.send(
                    CardEntryAction.ValidationStatusChanged(
                        model,
                        isValid
                    )
                )
            }
            onCardEntryButtonClickListener = {
                dismissKeyboard()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        viewModel.send(CardEntryAction.SubmitCardEntryForm)
                    },
                    KEYBOARD_DISMISS_TIMEOUT
                )
            }
        }
        billingDetailsFormView.apply {
            onFormValidationStatusListener = { model, isValid ->
                viewModel.send(
                    CardEntryAction.BillingDetailsValidationStatusChanged(
                        model,
                        isValid
                    )
                )
            }
            onBillingDetailsBackButtonClickListener =
                { viewModel.send(CardEntryAction.PressBackButton) }
            onBillingDetailsSubmitButtonClickListener =
                { viewModel.send(CardEntryAction.SubmitBillingDetailsForm) }
        }

        // sets bottomAppBar elevation based on scroll
        billingDetailsScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
                val autoTransition = AutoTransition()
                autoTransition.duration = BOTTOM_APP_BAR_ELEVATION_CHANGE_DURATION
                TransitionManager.beginDelayedTransition(billingContainer, autoTransition)
                if (v.canScrollVertically(1)) {
                    billingDetailsBottomAppBar.elevation = resources.getDimension(R.dimen.elevation_4)
                } else {
                    billingDetailsBottomAppBar.elevation = resources.getDimension(R.dimen.elevation_0)
                }
            }
        )

        // Prevents bottom sheet dialog to jump around when animating.
        val transition = LayoutTransition()
        transition.setAnimateParentHierarchy(false)
        bottomSheetContainer.layoutTransition = transition
    }

    override fun onStart() {
        super.onStart()
        subscribeToInsetsChanges()
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
        cardTransactionService.destroy()
        super.onDestroy()
    }

    private fun onUserCancelled() {
        // disable the button
        cancelButton.isEnabled = false

        if (judo.paymentWidgetType.isPaymentMethodsWidget) {
            findNavController().popBackStack()
        } else {
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled())
        }
    }

    private fun updateWithModel(model: CardEntryFragmentModel) {
        if (model.displayScanButton) {
            scanCardButton.visibility = View.VISIBLE
        } else {
            scanCardButton.visibility = View.GONE
        }
        if (model.displayBackButton) {
            billingDetailsBackButton.visibility = View.VISIBLE
        } else {
            billingDetailsBackButton.visibility = View.GONE
        }
        formView.model = model.formModel.cardDetailsInputModel
        billingDetailsFormView.model = model.formModel.billingDetailsInputModel
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
            is JudoPaymentResult.Error -> sharedViewModel.paymentResult.postValue(
                JudoPaymentResult.Error(
                    result.error
                )
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

    private fun handleScanCardButtonClicks() {
        val activity = requireActivity()
        val intent = ScanCardIntent.Builder(activity).build()
        activity.startActivityForResult(intent, SCAN_CARD_REQUEST_CODE)
    }

    private fun unSubscribeFromInsetsChanges() = requireDialog().window?.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            decorView.setOnApplyWindowInsetsListener(null)
        }
    }

    // Animating view position based on the keyboard show/hide state
    private fun subscribeToInsetsChanges() = requireDialog().window?.apply {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val sceneRoot =
                decorView.findViewById<View>(Window.ID_ANDROID_CONTENT)?.parent as? ViewGroup

            val insetsListener = View.OnApplyWindowInsetsListener { view, insets ->
                sceneRoot?.let { TransitionManager.beginDelayedTransition(it, ChangeBounds()) }
                return@OnApplyWindowInsetsListener if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.onApplyWindowInsets(insets)
                } else {
                    return@OnApplyWindowInsetsListener null
                }
            }

            decorView.setOnApplyWindowInsetsListener(insetsListener)
        }
    }

    override fun onSuccess(success: JudoPaymentResult) {
        sharedViewModel.paymentResult.postValue((success))
    }

    override fun onFailure(error: JudoPaymentResult) {
        sharedViewModel.paymentResult.postValue((error))
    }

    private val bottomSheetDialog: BottomSheetDialog
        get() = dialog as BottomSheetDialog
}

package com.judopay.judokit.android.ui.cardentry

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.toCardVerificationModel
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.db.JudoRoomDatabase
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.isCardPaymentWidget
import com.judopay.judokit.android.model.isPaymentMethodsWidget
import com.judopay.judokit.android.ui.cardentry.model.FormFieldType
import com.judopay.judokit.android.ui.cardverification.THREE_DS_ONE_DIALOG_FRAGMENT_TAG
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCardVerificationDialogFragment
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCompletionCallback
import com.judopay.judokit.android.ui.paymentmethods.CARD_NETWORK
import kotlinx.android.synthetic.main.card_entry_fragment.*

class CardEntryFragment : BottomSheetDialogFragment(), ThreeDSOneCompletionCallback {

    private lateinit var viewModel: CardEntryViewModel
    private lateinit var service: JudoApiService
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun getTheme(): Int = R.style.JudoTheme_BottomSheetDialogTheme

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        val cardRepository = TokenizedCardRepository(tokenizedCardDao)
        service = JudoApiServiceFactory.createApiService(application, judo)
        val selectedCardNetwork = arguments?.getParcelable<CardNetwork>(CARD_NETWORK)
        val factory = CardEntryViewModelFactory(
            judo,
            service,
            cardRepository,
            selectedCardNetwork,
            application
        )

        viewModel = ViewModelProvider(this, factory).get(CardEntryViewModel::class.java)

        if (selectedCardNetwork != null) {
            viewModel.send(CardEntryAction.EnableFormFields(listOf(FormFieldType.SECURITY_NUMBER)))
        }

        viewModel.model.observe(viewLifecycleOwner, { updateWithModel(it) })
        viewModel.judoApiCallResult.observe(viewLifecycleOwner, { dispatchApiResult(it) })
        viewModel.securityCodeResult.observe(
            viewLifecycleOwner,
            {
                sharedViewModel.securityCodeResult.postValue(it)
                findNavController().popBackStack()
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
            this.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.card_entry_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton.setOnClickListener(this::onUserCancelled)
        scanCardButton.setOnClickListener { handleScanCardButtonClicks() }

        formView.apply {
            onFormValidationStatusListener = { model, isValid ->
                viewModel.send(CardEntryAction.ValidationStatusChanged(model, isValid))
            }
            onSubmitButtonClickListener = { viewModel.send(CardEntryAction.SubmitForm) }
        }
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
        onUserCancelled(cancelButton)
    }

    private fun onUserCancelled(view: View) {
        // disable the button
        view.isEnabled = false

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
        formView.model = model.formModel
    }

    private fun dispatchApiResult(result: JudoApiCallResult<Receipt>) {
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
        sharedViewModel.paymentResult.postValue(result.toJudoPaymentResult(resources))
    }

    private fun dispatchCardPaymentApiResult(result: JudoApiCallResult<Receipt>) {
        when (result) {
            is JudoApiCallResult.Success -> handleSuccess(result.data)
            is JudoApiCallResult.Failure -> handleFailure(result)
        }
    }

    private fun dispatchPaymentMethodsApiResult(result: JudoApiCallResult<Receipt>) {
        when (result) {
            is JudoApiCallResult.Success -> persistTokenizedCard(result)
            is JudoApiCallResult.Failure -> handleFailure(result)
        }
    }

    private fun persistTokenizedCard(result: JudoApiCallResult.Success<Receipt>) {
        val cardDetails = result.data?.cardDetails
        if (cardDetails != null) {
            viewModel.send(CardEntryAction.InsertCard(cardDetails))
            findNavController().popBackStack()
        } else {
            sharedViewModel.paymentResult.postValue(result.toJudoPaymentResult(resources))
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

    private fun handleSuccess(receipt: Receipt?) {
        if (receipt != null) {
            if (receipt.is3dSecureRequired) {
                ThreeDSOneCardVerificationDialogFragment(
                    service,
                    receipt.toCardVerificationModel(),
                    this
                ).show(childFragmentManager, THREE_DS_ONE_DIALOG_FRAGMENT_TAG)
            } else {
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Success(receipt.toJudoResult()))
            }
        }
    }

    private fun handleFailure(failure: JudoApiCallResult.Failure) {
        sharedViewModel.paymentResult.postValue(failure.toJudoPaymentResult(resources))
    }

    override fun onSuccess(success: JudoPaymentResult) {
        sharedViewModel.paymentResult.postValue((success))
    }

    override fun onFailure(error: JudoPaymentResult) {
        sharedViewModel.paymentResult.postValue((error))
    }
}

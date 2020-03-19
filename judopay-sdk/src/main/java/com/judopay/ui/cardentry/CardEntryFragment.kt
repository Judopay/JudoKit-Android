package com.judopay.ui.cardentry

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.judopay.JUDO_RECEIPT
import com.judopay.JudoSharedViewModel
import com.judopay.R
import com.judopay.api.error.ApiError
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.api.model.response.toJudoPaymentResult
import com.judopay.judo
import com.judopay.model.JudoPaymentResult
import com.judopay.model.isCardPaymentWidget
import com.judopay.model.isPaymentMethodsWidget
import kotlinx.android.synthetic.main.card_entry_fragment.*

class CardEntryFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: CardEntryViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun getTheme(): Int = R.style.JudoTheme_BottomSheetDialogTheme

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val factory = CardEntryViewModelFactory(application, judo)

        viewModel = ViewModelProvider(this, factory).get(CardEntryViewModel::class.java)

        viewModel.model.observe(viewLifecycleOwner, Observer { updateWithModel(it) })
        viewModel.judoApiCallResult.observe(viewLifecycleOwner, Observer { dispatchApiResult(it) })
    }

    // present it always expanded
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener {
                val bottomSheetDialog = dialog as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
                    .setState(BottomSheetBehavior.STATE_EXPANDED)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.card_entry_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton.setOnClickListener(this::onUserCancelled)
        scanCardButton.setOnClickListener(this::handleScanCardButtonClicks)

        formView.apply {
            onValidationPassedListener =
                { model -> viewModel.send(CardEntryAction.ValidationPassed(model)) }
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
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled)
        }
    }

    private fun updateWithModel(model: CardEntryFragmentModel) {
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
        sharedViewModel.paymentResult.postValue(result.toJudoPaymentResult())
    }

    private fun dispatchCardPaymentApiResult(result: JudoApiCallResult<Receipt>) {
        when (result) {
            is JudoApiCallResult.Success -> handleSuccess(result.data)
            is JudoApiCallResult.Failure -> if (result.error != null) sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(result.error))
        }
    }

    private fun dispatchPaymentMethodsApiResult(result: JudoApiCallResult<Receipt>) {
        when (result) {
            is JudoApiCallResult.Success -> persistTokenizedCard(result)
            is JudoApiCallResult.Failure -> presentError(result.error)
        }
    }

    private fun persistTokenizedCard(result: JudoApiCallResult.Success<Receipt>) {
        val cardDetails = result.data?.cardDetails
        if (cardDetails != null) {
            viewModel.send(CardEntryAction.InsertCard(cardDetails))
            findNavController().popBackStack()
        } else {
            presentError()
        }
    }

    private fun presentError(apiError: ApiError? = null) {
        val message = apiError?.message
            ?: requireContext().getString(R.string.unable_to_process_request_error_desc)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.unable_to_process_request_error_title)
            .setMessage(message)
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private fun handleScanCardButtonClicks(view: View) {
        view.isEnabled = false
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
        if (receipt != null)
            if (receipt.is3dSecureRequired) {
                findNavController().navigate(
                    R.id.action_cardEntryFragment_to_cardVerificationFragment, bundleOf(
                        JUDO_RECEIPT to receipt
                    )
                )
            } else {
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Success(receipt))
            }
    }
}

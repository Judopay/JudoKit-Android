package com.judopay.judokit.android.ui.editcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.judopay.judokit.android.R
import com.judopay.judokit.android.cardRepository
import com.judopay.judokit.android.databinding.EditCardFragmentBinding
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.ui.common.LengthFilter
import com.judopay.judokit.android.ui.common.viewModelFactory
import com.judopay.judokit.android.ui.editcard.adapter.ColorPickerAdapter
import com.judopay.judokit.android.ui.editcard.adapter.ColorPickerItem
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

const val JUDO_TOKENIZED_CARD_ID = "com.judopay.judokit.android.judo-tokenized-card-id"

data class EditCardModel(
    val colorOptions: List<ColorPickerItem>,
    val isSaveButtonEnabled: Boolean,
    val card: PaymentCardViewModel,
    var title: String,
    val isDefault: Boolean,
)

private const val CARD_TITLE_MAX_CHARACTERS = 28

class EditCardFragment : Fragment() {
    private lateinit var viewModel: EditCardViewModel
    private var viewBinding: EditCardFragmentBinding? = null
    private val binding get() = viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = EditCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewModelObserving()
        setupColorPicker()
        setupCallbackListeners()
    }

    private fun initializeViewModel() {
        val cardId = arguments?.getInt(JUDO_TOKENIZED_CARD_ID) ?: -1
        val factory = viewModelFactory { EditCardViewModel(cardId, cardRepository(), requireActivity().application) }
        viewModel = ViewModelProvider(this, factory)[EditCardViewModel::class.java]
    }

    private fun initializeViewModelObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.filterNotNull().collect { updateWithModel(it) }
            }
        }
    }

    private fun updateWithModel(model: EditCardModel) {
        // setup colors
        (binding.colorPickerRecyclerView.adapter as? ColorPickerAdapter)?.items = model.colorOptions

        binding.saveButton.isEnabled = model.isSaveButtonEnabled

        binding.titleEditText.apply {
            if (text.toString() != model.title) {
                setText(model.title)
                setSelection(text.length)
            }
        }

        val checkMark = if (model.isDefault) R.drawable.ic_radio_on else R.drawable.ic_radio_off
        binding.saveAsDefaultTextView.setCompoundDrawablesWithIntrinsicBounds(checkMark, 0, 0, 0)

        binding.cardView.model = model.card
    }

    private fun setupCallbackListeners() {
        binding.backButton.setOnClickListener(::dismiss)

        binding.cancelButton.setOnClickListener(::dismiss)

        binding.saveButton.setOnClickListener {
            viewModel.send(EditCardAction.Save)
            dismiss(binding.saveButton)
        }

        binding.saveAsDefaultTextView.setOnClickListener { viewModel.send(EditCardAction.ToggleDefaultCardState) }

        binding.titleTextInputLayout.error =
            getString(R.string.jp_error_card_title_too_long, CARD_TITLE_MAX_CHARACTERS)

        binding.titleEditText.doAfterTextChanged {
            viewModel.send(EditCardAction.ChangeTitle(it.toString()))
        }

        binding.titleEditText.filters =
            arrayOf(
                LengthFilter(CARD_TITLE_MAX_CHARACTERS) {
                    binding.titleTextInputLayout.isErrorEnabled = it
                },
            )

        binding.titleEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.titleTextInputLayout.isErrorEnabled = false
            }
        }
    }

    private fun dismiss(view: View) {
        view.isEnabled = false
        view.dismissKeyboard()
        findNavController().popBackStack()
    }

    private fun setupColorPicker() {
        val context = requireContext()
        val colorPickerAdapter =
            ColorPickerAdapter(emptyList()) {
                viewModel.send(EditCardAction.ChangePattern(it.pattern))
            }

        colorPickerAdapter.setHasStableIds(true)

        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.colorPickerRecyclerView.apply {
            adapter = colorPickerAdapter
            layoutManager = manager
        }
    }
}

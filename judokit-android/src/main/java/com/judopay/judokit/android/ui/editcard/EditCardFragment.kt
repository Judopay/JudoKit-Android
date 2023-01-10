package com.judopay.judokit.android.ui.editcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.EditCardFragmentBinding
import com.judopay.judokit.android.db.JudoRoomDatabase
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.dismissKeyboard
import com.judopay.judokit.android.ui.common.LengthFilter
import com.judopay.judokit.android.ui.editcard.adapter.ColorPickerAdapter
import com.judopay.judokit.android.ui.editcard.adapter.ColorPickerItem
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel

const val JUDO_TOKENIZED_CARD_ID = "com.judopay.judokit.android.judo-tokenized-card-id"

data class EditCardModel(
    val colorOptions: List<ColorPickerItem>,
    val isSaveButtonEnabled: Boolean,
    val card: PaymentCardViewModel,
    var title: String,
    val isDefault: Boolean
)

private const val CARD_TITLE_MAX_CHARACTERS = 28

class EditCardFragment : Fragment() {
    private lateinit var viewModel: EditCardViewModel
    private var _binding: EditCardFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EditCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val cardId = arguments?.getInt(JUDO_TOKENIZED_CARD_ID) ?: -1

        val application = requireActivity().application
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        val cardRepository = TokenizedCardRepository(tokenizedCardDao)
        val factory = EditCardViewModelFactory(cardId, cardRepository, application)

        viewModel = ViewModelProvider(this, factory).get(EditCardViewModel::class.java)
        viewModel.model.observe(viewLifecycleOwner) { updateWithModel(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorPicker()
        setupCallbackListeners()
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
            getString(R.string.error_card_title_too_long, CARD_TITLE_MAX_CHARACTERS)

        binding.titleEditText.doAfterTextChanged {
            viewModel.send(EditCardAction.ChangeTitle(it.toString()))
        }

        binding.titleEditText.filters = arrayOf(
            LengthFilter(CARD_TITLE_MAX_CHARACTERS) {
                binding.titleTextInputLayout.isErrorEnabled = it
            }
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
        val colorPickerAdapter = ColorPickerAdapter(emptyList()) {
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

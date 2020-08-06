package com.judokit.android.ui.editcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.judokit.android.R
import com.judokit.android.db.JudoRoomDatabase
import com.judokit.android.db.repository.TokenizedCardRepository
import com.judokit.android.dismissKeyboard
import com.judokit.android.ui.common.LengthFilter
import com.judokit.android.ui.editcard.adapter.ColorPickerAdapter
import com.judokit.android.ui.editcard.adapter.ColorPickerItem
import com.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import kotlinx.android.synthetic.main.edit_card_fragment.*

const val JUDO_TOKENIZED_CARD_ID = "com.judokit.android.judo-tokenized-card-id"

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_card_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val cardId = arguments?.getInt(JUDO_TOKENIZED_CARD_ID) ?: -1

        val application = requireActivity().application
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        val cardRepository = TokenizedCardRepository(tokenizedCardDao)
        val factory = EditCardViewModelFactory(cardId, cardRepository, application)

        viewModel = ViewModelProvider(this, factory).get(EditCardViewModel::class.java)
        viewModel.model.observe(viewLifecycleOwner, Observer { updateWithModel(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorPicker()
        setupCallbackListeners()
    }

    private fun updateWithModel(model: EditCardModel) {
        // setup colors
        (colorPickerRecyclerView.adapter as? ColorPickerAdapter)?.items = model.colorOptions

        saveButton.isEnabled = model.isSaveButtonEnabled

        titleEditText.apply {
            if (text.toString() != model.title) {
                setText(model.title)
                setSelection(text.length)
            }
        }

        val checkMark = if (model.isDefault) R.drawable.ic_radio_on else R.drawable.ic_radio_off
        saveAsDefaultTextView.setCompoundDrawablesWithIntrinsicBounds(checkMark, 0, 0, 0)

        cardView.model = model.card
    }

    private fun setupCallbackListeners() {
        backButton.setOnClickListener(::dismiss)

        cancelButton.setOnClickListener(::dismiss)

        saveButton.setOnClickListener {
            viewModel.send(EditCardAction.Save)
            dismiss(saveButton)
        }

        saveAsDefaultTextView.setOnClickListener { viewModel.send(EditCardAction.ToggleDefaultCardState) }

        titleTextInputLayout.error =
            getString(R.string.error_card_title_too_long, CARD_TITLE_MAX_CHARACTERS)

        titleEditText.doAfterTextChanged {
            viewModel.send(EditCardAction.ChangeTitle(it.toString()))
        }

        titleEditText.filters = arrayOf(
            LengthFilter(CARD_TITLE_MAX_CHARACTERS) {
                titleTextInputLayout.isErrorEnabled = it
            }
        )

        titleEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                titleTextInputLayout.isErrorEnabled = false
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

        colorPickerRecyclerView.apply {
            adapter = colorPickerAdapter
            layoutManager = manager
        }
    }
}

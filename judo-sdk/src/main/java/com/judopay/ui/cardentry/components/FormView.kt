package com.judopay.ui.cardentry.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.judopay.parentOfType
import com.judopay.subViewsWithType

enum class FormFieldType {
    NUMBER,
    HOLDER_NAME,
    EXPIRATION_DATE,
    SECURITY_NUMBER,
    COUNTRY,
    POST_CODE,
    SUBMIT
}

data class FormModel(
        val cardNumber: String = "",
        val cardHolderName: String = "",
        val expirationDate: String = "",
        val securityNumber: String = "",
        val country: String = "",
        val postCode: String = ""
)

internal fun FormModel.getValueForFieldType(type: FormFieldType): String = when (type) {
    FormFieldType.NUMBER -> cardNumber
    FormFieldType.HOLDER_NAME -> cardHolderName
    FormFieldType.EXPIRATION_DATE -> expirationDate
    FormFieldType.SECURITY_NUMBER -> securityNumber
    FormFieldType.COUNTRY -> country
    FormFieldType.POST_CODE -> postCode
    else -> ""
}

data class InputFieldConfiguration(override val type: FormFieldType) : FieldConfiguration

data class SubmitFieldConfiguration(override val type: FormFieldType = FormFieldType.SUBMIT, val text: String) : FieldConfiguration

interface FieldConfiguration {
    val type: FormFieldType
}

data class FormViewModel(
        val formModel: FormModel,
        val enabledFields: List<FormFieldType>,
        val fieldMappings: Map<Int, FieldConfiguration>
)

class FormView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    var model = FormViewModel(FormModel(), emptyList(), emptyMap())
        set(value) {
            field = value
            update()
        }

    private val formFieldMapping = mutableMapOf<FormFieldType, TextInputEditText>()
    private var submitButton: Button? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupFields()
    }

    private fun setupFields() {
        val inputEditText = subViewsWithType(TextInputEditText::class.java)
        val buttons = subViewsWithType(MaterialButton::class.java)

        inputEditText.forEach { editText ->
            // configure the filed only if we know about it
            model.fieldMappings[editText.id]?.let { config ->
                configureEditText(editText, config)
            }
        }

        buttons.forEach { button ->
            model.fieldMappings[button.id]?.let { config ->
                configureSubmitButton(button, config as SubmitFieldConfiguration)
            }
        }
    }

    private fun update() {
        formFieldMapping.clear()
        setupFields()
    }

    private fun configureSubmitButton(button: Button, config: SubmitFieldConfiguration) {
        submitButton = button
        button.text = config.text
    }

    private fun configureEditText(editText: TextInputEditText, config: FieldConfiguration) {
        // keep a reference to the editText for later to extract the input data
        formFieldMapping[config.type] = editText

        // if the field is disabled, hide it from the layout
        val visibility = if (model.enabledFields.contains(config.type)) View.VISIBLE else View.GONE
        editText.parentOfType(TextInputLayout::class.java)?.visibility = visibility

        // setup field properties
        editText.setText(model.formModel.getValueForFieldType(config.type))
        editText.hint = "My hint"
    }

}
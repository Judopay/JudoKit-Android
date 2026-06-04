package com.judopay.judokit.android.ui.editcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.ui.editcard.adapter.ColorPickerItem
import com.judopay.judokit.android.ui.paymentmethods.toPaymentCardViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EditCardAction {
    data class ChangePattern(
        val pattern: CardPattern,
    ) : EditCardAction()

    data class ChangeTitle(
        val newTitle: String,
    ) : EditCardAction()

    object ToggleDefaultCardState : EditCardAction()

    object Save : EditCardAction()
}

private const val MAX_CARD_TITLE_LENGTH = 28

class EditCardViewModel(
    private val cardId: Int,
    private val cardRepository: TokenizedCardRepository,
    application: Application,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<EditCardModel?>(null)
    val uiState: StateFlow<EditCardModel?> = _uiState.asStateFlow()

    private val patterns = CardPattern.entries.toTypedArray()
    private val cachedPatternItems = patterns.map { ColorPickerItem(it) }

    // mutable data
    private var cardEntity: TokenizedCardEntity? = null

    private var selectedPattern: CardPattern = patterns.first()
    private var isSelectedAsDefault: Boolean = false
    private var currentCardTitle: String = ""

    private val isSaveButtonEnabled: Boolean
        get() {
            val entity = cardEntity ?: return false
            return (
                selectedPattern != entity.pattern ||
                    isSelectedAsDefault != entity.isDefault ||
                    currentCardTitle != entity.title
            ) &&
                currentCardTitle.length <= MAX_CARD_TITLE_LENGTH
        }

    init {
        loadCardEntity()
    }

    fun send(action: EditCardAction) {
        when (action) {
            is EditCardAction.ChangePattern -> buildModel(action.pattern)
            is EditCardAction.Save -> persistChanges()
            is EditCardAction.ToggleDefaultCardState -> {
                isSelectedAsDefault = !isSelectedAsDefault
                buildModel()
            }
            is EditCardAction.ChangeTitle -> buildModel(newTitle = action.newTitle)
        }
    }

    private fun buildModel(
        newPattern: CardPattern = this.selectedPattern,
        newTitle: String = currentCardTitle,
    ) {
        val entity = cardEntity ?: return
        selectedPattern = newPattern
        currentCardTitle = newTitle

        cachedPatternItems.forEach { it.isSelected = it.pattern == selectedPattern }

        _uiState.value =
            EditCardModel(
                cachedPatternItems,
                isSaveButtonEnabled,
                entity.toPaymentCardViewModel(currentCardTitle, selectedPattern),
                title = newTitle,
                isDefault = isSelectedAsDefault,
            )
    }

    private fun loadCardEntity() =
        viewModelScope.launch {
            val entity = cardRepository.findWithId(cardId)
            cardEntity = entity
            selectedPattern = entity.pattern
            isSelectedAsDefault = entity.isDefault
            currentCardTitle = entity.title
            buildModel()
        }

    private fun persistChanges() =
        viewModelScope.launch {
            val entity = cardEntity ?: return@launch
            cardRepository.insert(
                entity.apply {
                    title = currentCardTitle
                    pattern = selectedPattern
                    isDefault = isSelectedAsDefault
                },
            )
        }
}

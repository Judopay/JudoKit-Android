package com.judopay.ui.editcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.R
import com.judopay.db.JudoRoomDatabase
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.db.repository.TokenizedCardRepository
import com.judopay.ui.editcard.adapter.ColorPickerItem
import kotlinx.coroutines.launch

sealed class EditCardAction {
    data class ChangeColor(val color: Int) : EditCardAction()
    data class ChangeTitle(val newTitle: String) : EditCardAction()

    object ToggleDefaultCardState : EditCardAction()
    object Save : EditCardAction()
}

internal class EditCardViewModelFactory(
    private val application: Application,
    private val cardId: Int
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EditCardViewModel::class.java) {
            EditCardViewModel(application, cardId) as T
        } else super.create(modelClass)
    }
}

class EditCardViewModel(
    application: Application,
    private val cardId: Int
) : AndroidViewModel(application) {

    val model = MutableLiveData<EditCardModel>()

    private val context = application
    private val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
    private val cardRepository = TokenizedCardRepository(tokenizedCardDao)

    private val colors = context.resources.getIntArray(R.array.card_colors)
    private val cachedColorItems = colors.map { ColorPickerItem(it) }

    // mutable data
    private lateinit var cardEntity: TokenizedCardEntity

    private var selectedColor: Int = colors.first()
    private var isSelectedAsDefault: Boolean = false
    private var currentCardTitle: String = ""

    private val isSaveButtonEnabled: Boolean
        get() {
            return selectedColor != cardEntity.color ||
                isSelectedAsDefault != cardEntity.isDefault ||
                currentCardTitle != cardEntity.title
        }

    init {
        loadCardEntity()
    }

    fun send(action: EditCardAction) {
        when (action) {
            is EditCardAction.ChangeColor -> buildModel(action.color)
            is EditCardAction.Save -> persistChanges()
            is EditCardAction.ToggleDefaultCardState -> {
                isSelectedAsDefault = !isSelectedAsDefault
                buildModel()
            }
            is EditCardAction.ChangeTitle -> buildModel(newTitle = action.newTitle)
        }
    }

    private fun buildModel(
        newColor: Int = this.selectedColor,
        newTitle: String = currentCardTitle
    ) {
        selectedColor = newColor
        currentCardTitle = newTitle

        cachedColorItems.forEach { it.isSelected = it.color == selectedColor }

        model.postValue(
            EditCardModel(
                cachedColorItems,
                isSaveButtonEnabled,
                title = newTitle,
                isDefault = isSelectedAsDefault
            )
        )
    }

    private fun loadCardEntity() = viewModelScope.launch {
        cardEntity = cardRepository.findWithId(cardId)
        selectedColor = cardEntity.color
        isSelectedAsDefault = cardEntity.isDefault
        currentCardTitle = cardEntity.title

        buildModel()
    }

    private fun persistChanges() = viewModelScope.launch {
        cardRepository.insert(cardEntity.apply {
            title = currentCardTitle
            color = selectedColor
            isDefault = isSelectedAsDefault
        })
    }
}

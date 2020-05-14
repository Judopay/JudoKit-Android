package com.judokit.android.ui.editcard

import android.app.Application
import androidx.lifecycle.Observer
import com.judokit.android.InstantExecutorExtension
import com.judokit.android.db.entity.TokenizedCardEntity
import com.judokit.android.db.repository.TokenizedCardRepository
import com.judokit.android.model.CardNetwork
import com.judokit.android.ui.editcard.adapter.ColorPickerItem
import com.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private const val CARD_ID = 1
private const val NEW_TITLE = "NEW_TITLE"

@ExperimentalCoroutinesApi
@ExtendWith(com.judokit.android.InstantExecutorExtension::class)
@DisplayName("Testing edit card view model logic")
internal class EditCardViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val application: Application = mockk(relaxed = true)
    private val cardRepository: TokenizedCardRepository = mockk(relaxed = true)
    private val editCardModelMock = spyk<Observer<EditCardModel>>()
    private val tokenizedCardEntity: TokenizedCardEntity = mockk(relaxed = true) {
        every { network } returns CardNetwork.VISA
        every { pattern } returns CardPattern.BLACK
    }

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("com.judokit.android.ui.editcard.MappersKt")

        coEvery { cardRepository.findWithId(CARD_ID) } returns tokenizedCardEntity
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    @DisplayName("Given edit card view model is initialised, then call repository.findWithId(cardId)")
    fun findCardWithGivenIdOnViewModelInitialised() {
        EditCardViewModel(CARD_ID, cardRepository, application)

        coVerify { cardRepository.findWithId(CARD_ID) }
    }

    @Test
    @DisplayName("Given edit card view model is initialised, then should update model with new EditCardModel")
    fun updateModelOnEditCardViewModelInitialised() {
        val slots = mutableListOf<EditCardModel>()

        val sut = EditCardViewModel(CARD_ID, cardRepository, application)

        sut.model.observeForever(editCardModelMock)

        verify { editCardModelMock.onChanged(capture(slots)) }

        val editCardModel = slots[0]

        assertEquals(getEditCardModel(), editCardModel)
    }

    @Test
    @DisplayName("Given send method is called with ChangePattern action, then update model with new pattern")
    fun updateModelWithNewPatternOnChangePatternAction() {
        val slots = mutableListOf<EditCardModel>()

        val sut = EditCardViewModel(CARD_ID, cardRepository, application)

        sut.send(EditCardAction.ChangePattern(CardPattern.BLACK))

        sut.model.observeForever(editCardModelMock)

        verify { editCardModelMock.onChanged(capture(slots)) }

        val editCardModel = slots[0]

        assertEquals(getEditCardModel(), editCardModel)
    }

    @Test
    @DisplayName("Given send method is called with Save action, then call cardRepository.insert(cardEntity)")
    fun insertCardOnSaveAction() {
        val sut = EditCardViewModel(CARD_ID, cardRepository, application)

        sut.send(EditCardAction.Save)

        sut.model.observeForever(editCardModelMock)

        coVerify { cardRepository.insert(tokenizedCardEntity) }
    }

    @Test
    @DisplayName("Given send method is called with ToggleDefaultCardState action, then update isDefault model field")
    fun updateIsDefaultModelFieldOnToggleDefaultCardStateAction() {
        val slots = mutableListOf<EditCardModel>()

        val sut = EditCardViewModel(CARD_ID, cardRepository, application)

        sut.send(EditCardAction.ToggleDefaultCardState)

        sut.model.observeForever(editCardModelMock)

        verify { editCardModelMock.onChanged(capture(slots)) }

        val editCardModel = slots[0]

        assertEquals(getEditCardModel(onChanged = true, isDefault = true), editCardModel)
    }

    @Test
    @DisplayName("Given send method is called with ChangeTitle action, then update title model field")
    fun updateTitleModelFieldOnChangeTitleAction() {
        val slots = mutableListOf<EditCardModel>()

        val sut = EditCardViewModel(CARD_ID, cardRepository, application)

        sut.send(EditCardAction.ChangeTitle(NEW_TITLE))

        sut.model.observeForever(editCardModelMock)

        verify { editCardModelMock.onChanged(capture(slots)) }

        val editCardModel = slots[0]

        assertEquals(getEditCardModel(onChanged = true, title = NEW_TITLE), editCardModel)
    }

    private val patterns = CardPattern.values().map { ColorPickerItem(it) }

    private fun getEditCardModel(
        onChanged: Boolean = false,
        isDefault: Boolean = false,
        title: String = ""
    ): EditCardModel {
        patterns.forEach { it.isSelected = it.pattern == CardPattern.BLACK }

        return EditCardModel(
            colorOptions = patterns,
            isSaveButtonEnabled = onChanged,
            card = PaymentCardViewModel(name = title),
            title = title,
            isDefault = isDefault
        )
    }
}

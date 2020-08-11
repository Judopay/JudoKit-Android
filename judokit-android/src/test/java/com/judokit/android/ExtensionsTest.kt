package com.judokit.android

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.judokit.android.model.ApiEnvironment
import com.judokit.android.ui.error.JudoNotProvidedError
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Test com.judokit.android.Extensions")
internal class ExtensionsTest {

    @DisplayName("Given judo.isSandboxed is true, then return ApiEnvironment.SANDBOX.host")
    @Test
    fun returnSandboxHostWhenIsSandboxedTrue() {
        val judo: Judo = mockk(relaxed = true) {
            every { isSandboxed } returns true
        }
        assertEquals(ApiEnvironment.SANDBOX.host, judo.apiBaseUrl)
    }

    @DisplayName("Given judo.isSandboxed is false, then return ApiEnvironment.LIVE.host")
    @Test
    fun returnLiveHostWhenIsSandboxedFalse() {
        val judo: Judo = mockk(relaxed = true) {
            every { isSandboxed } returns false
        }
        assertEquals(ApiEnvironment.LIVE.host, judo.apiBaseUrl)
    }

    @DisplayName("Given requireNotNullOrEmpty is called , when value parameter is null, then throw IllegalArgumentException")
    @Test
    fun throwsExceptionWhenRequireNotNullOrEmptyValueNull() {
        assertThrows<IllegalArgumentException> { requireNotNullOrEmpty(null, "propertyName") }
    }

    @DisplayName("Given requireNotNullOrEmpty is called, when value parameter is empty, then throw IllegalArgumentException")
    @Test
    fun throwsExceptionWhenRequireNotNullOrEmptyValueEmpty() {
        assertThrows<IllegalArgumentException> { requireNotNullOrEmpty("", "propertyName") }
    }

    @DisplayName("Given requireNotNullOrEmpty is called, when value parameter is valid, then return value")
    @Test
    fun returnValueWhenRequireNotNullOrEmptyValueValid() {
        assertEquals("Value", requireNotNullOrEmpty("Value", "PropertyName"))
    }

    @DisplayName("Given requireNotNull is called, when value parameter is null, then throw IllegalArgumentException")
    @Test
    fun throwsExceptionWhenRequireNotNullValueNull() {
        assertThrows<IllegalArgumentException> { requireNotNull(null, "propertyName") }
    }

    @DisplayName("Given requireNotNull is called, when value parameter is valid, then return value")
    @Test
    fun returnValueWhenRequireNotNullValueValid() {
        val any: Any? = Any()
        assertEquals(any, requireNotNull(any), "propertyName")
    }

    @DisplayName("Given view.parentOfType is called, when parameter is LinearLayout::class, then return view's parent LinearLayout")
    @Test
    fun returnViewParent() {
        val expectedParent: LinearLayout = mockk(relaxed = true)
        val view: View = mockk(relaxed = true) {
            every { parent } returns expectedParent
        }

        assertEquals(expectedParent, view.parentOfType(LinearLayout::class.java))
    }

    @DisplayName("Given view.parentOfType is called, when view's parent is not of parentType, then return null")
    @Test
    fun returnNullOnViewsParentNotParentType() {
        val expectedParent: LinearLayout = mockk(relaxed = true)
        val view: View = mockk(relaxed = true) {
            every { parent } returns expectedParent
        }

        assertEquals(null, view.parentOfType(ViewGroup::class.java))
    }

    @DisplayName("Given view.parentOfType is called, when view's parent is not a View, then return null")
    @Test
    fun returnNullOnViewsParentNotView() {
        val expectedParent: ViewGroup = mockk(relaxed = true)
        val view: View = mockk(relaxed = true) {
            every { parent } returns expectedParent
        }

        assertEquals(null, view.parentOfType(ViewGroup::class.java))
    }

    @DisplayName("Given FragmentActivity.judo is called, then get judo object")
    @Test
    fun getJudoObjectOnFragmentActivityJudoCall() {
        val expectedJudoObject: Judo = mockk(relaxed = true)
        val fragment: FragmentActivity = mockk(relaxed = true) {
            every { intent.getParcelableExtra<Judo>(JUDO_OPTIONS) } returns expectedJudoObject
        }

        assertEquals(expectedJudoObject, fragment.judo)
    }

    @DisplayName("Given FragmentActivity.judo is called, when judo object is null, then throw JudoNotProvidedError")
    @Test
    fun throwJudoNotProvidedErrorOnJudoObjectNull() {
        val fragment: FragmentActivity = mockk(relaxed = true) {
            every { intent.getParcelableExtra<Judo>(JUDO_OPTIONS) } returns null
        }

        assertThrows<JudoNotProvidedError> { fragment.judo }
    }

    @DisplayName("Given Fragment.judo is called, then get judo object")
    @Test
    fun getJudoObjectOnFragmentJudoCall() {
        val expectedJudoObject: Judo = mockk(relaxed = true)
        val fragment: Fragment = mockk(relaxed = true) {
            every { requireActivity().judo } returns expectedJudoObject
        }

        assertEquals(expectedJudoObject, fragment.judo)
    }

    @DisplayName("Given String.withWhitespacesRemoved is called, then return string with no whitespace")
    @Test
    fun returnStringWithNoWhitespace() {
        val whitespaceString = "White space"

        assertEquals("Whitespace", whitespaceString.withWhitespacesRemoved)
    }

//    @DisplayName("Given ")
//    @Test
//    fun w(){
//        every { PaymentRequest.Builder().build() } returns
//    }
}

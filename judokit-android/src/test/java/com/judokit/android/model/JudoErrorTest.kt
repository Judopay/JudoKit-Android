package com.judokit.android.model

import android.content.res.Resources
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing JudoError static methods")
internal class JudoErrorTest {

    private val resources: Resources = mockk(relaxed = true)

    @DisplayName("Given userCancelled is called, then return default cancel error")
    @Test
    fun returnDefaultCancelErrorOnUserCancelled() {
        assertEquals(USER_CANCELLED, JudoError.userCancelled(resources).code)
    }
}

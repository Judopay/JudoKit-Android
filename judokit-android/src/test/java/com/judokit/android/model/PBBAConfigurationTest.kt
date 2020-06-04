package com.judokit.android.model

import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing PBBAConfiguration builder")
internal class PBBAConfigurationTest {

    private val sut = PBBAConfiguration.Builder()
        .setAppearsOnStatementAs("appearOnStatementAs")
        .setDeepLinkScheme("scheme")
        .setDeepLinkURL(mockk(relaxed = true))
        .setEmailAddress("emailAddress")
        .setMobileNumber("mobileNumber")

    @DisplayName("Given deep link scheme is null, then throw exception")
    @Test
    fun throwsExceptionOnDeepLinkSchemeNull() {
        assertThrows<IllegalArgumentException> { sut.setDeepLinkScheme(null).build() }
    }

    @Test
    @DisplayName("Given build is called, when all required fields are valid, then exception not thrown")
    fun exceptionNotThrownOnBuildWithAllFieldsValid() {
        Assertions.assertDoesNotThrow { sut.build() }
    }
}

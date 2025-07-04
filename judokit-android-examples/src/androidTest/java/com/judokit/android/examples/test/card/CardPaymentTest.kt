package com.judokit.android.examples.test.card

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.judokit.android.examples.BuildConfig
import com.judokit.android.examples.feature.DemoFeatureListActivity
import com.judokit.android.examples.feature.tokenpayments.TokenPaymentsActivity
import com.judokit.android.examples.result.ResultActivity
import com.judokit.android.examples.test.card.BillingInfo.INVALID_ADDRESS_LABEL
import com.judokit.android.examples.test.card.BillingInfo.INVALID_CITY_LABEL
import com.judokit.android.examples.test.card.BillingInfo.INVALID_EMAIL_LABEL
import com.judokit.android.examples.test.card.BillingInfo.INVALID_PHONE_LABEL
import com.judokit.android.examples.test.card.BillingInfo.INVALID_POSTCODE
import com.judokit.android.examples.test.card.BillingInfo.INVALID_POSTCODE_LABEL
import com.judokit.android.examples.test.card.BillingInfo.INVALID_ZIPCODE_LABEL
import com.judokit.android.examples.test.card.BillingInfo.SPECIAL_CHARACTERS
import com.judokit.android.examples.test.card.BillingInfo.VALID_ADDRESS
import com.judokit.android.examples.test.card.BillingInfo.VALID_CITY
import com.judokit.android.examples.test.card.BillingInfo.VALID_COUNTRY
import com.judokit.android.examples.test.card.BillingInfo.VALID_COUNTRY_CODE
import com.judokit.android.examples.test.card.BillingInfo.VALID_EMAIL
import com.judokit.android.examples.test.card.BillingInfo.VALID_MOBILE
import com.judokit.android.examples.test.card.BillingInfo.VALID_POSTCODE
import com.judokit.android.examples.test.card.CardDetails.CARD_EXPIRY
import com.judokit.android.examples.test.card.CardDetails.CARD_NUMBER
import com.judokit.android.examples.test.card.CardDetails.CARD_SECURITY_CODE
import com.judokit.android.examples.test.card.CardDetails.WRONG_CV2
import com.judokit.android.examples.test.card.CardPaymentTest.ValidCardDetails.CARDHOLDER_NAME
import com.judokit.android.examples.test.card.FeaturesList.CHECK_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PAYMENT_METHODS_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PAY_WITH_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PREAUTH_METHODS_LABEL
import com.judokit.android.examples.test.card.FeaturesList.PREAUTH_WITH_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.REGISTER_CARD_LABEL
import com.judokit.android.examples.test.card.FeaturesList.TOKEN_PAYMENTS_LABEL
import com.judokit.android.examples.test.card.Other.CANCELLED_PAYMENT_TOAST
import com.judokit.android.examples.test.card.Other.CANCEL_BUTTON
import com.judokit.android.examples.test.card.annotations.SmokeTest
import com.judopay.judokit.android.R
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardPaymentTest {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    object ValidCardDetails {
        const val CARD_NUMBER = "4976 3500 0000 6891"
        const val CARDHOLDER_NAME = "Test User"
        const val CARD_EXPIRY = "12/25"
        const val CARD_SECURITY_CODE = "341"
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(DemoFeatureListActivity::class.java)

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    @Before
    fun setupSettings() {
        sharedPrefs
            .edit()
            .apply {
                putString("judo_id", BuildConfig.JUDO_ID)
                putString("challengeRequestIndicator", "CHALLENGE_AS_MANDATE")
                putBoolean("is_payment_session_enabled", false)
                putString("token", BuildConfig.API_TEST_TOKEN)
                putString("secret", BuildConfig.API_TEST_SECRET)
                putString("amount", "0.15")
                putString("currency", "GBP")
                putBoolean("should_ask_for_csc", false)
                putBoolean("is_recommendation_feature_enabled", false)
            }.commit()
    }

    @After
    fun tearDown() {
        toggleBillingInfoSetting(false)
    }

    @Test
    fun testValidCardDetailsInputSubmitButton() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
    }

    @Test
    fun testCancelledTransactionErrorPopupShouldBeDisplayed() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        onView(withText(CANCEL_BUTTON))
            .perform(click())

        Thread.sleep(500)

        onView(withText(CANCELLED_PAYMENT_TOAST)).check(matches(isDisplayed()))
    }

    @Test
    @SmokeTest
    fun testSuccessfulTransaction() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertOnView(withText("COMPLETE"))

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun testSuccessfulRegisterCardTransaction() {
        onView(withText(REGISTER_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertOnView(withText("COMPLETE"))

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Register")
    }

    @Test
    fun testDeclinedTransaction() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            "12/30",
            "123",
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertOnView(withText("COMPLETE"))

        clickCompleteOn3DS2Screen()

        assertReceiptObject("", "", "Declined", "Payment")
    }

    @Test
    fun testFailedTransaction() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            "4111 1111 1111 1111",
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            "123",
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("The gateway reported an error", "", "Error", "Payment")
    }

    @Test
    fun testCancel3DS2ChallengeScreen() {
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun("com.judopay.judo3ds2.ui.challenge.ChallengeActivity") {
            Thread.sleep(5000)
            onView(withText("CANCEL"))
                .perform(click())
        }

        awaitActivityThenRun(ResultActivity::class.java.name) {
            onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
                .check(matches(hasDescendant(withText("Unable to process transaction. Card authentication failed with 3DS Server."))))
        }
    }

    @Test
    fun testSuccessfulPreauthTransaction() {
        onView(withText(PREAUTH_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "PreAuth")
    }

    @Test
    fun testSuccessfulCheckCardTransaction() {
        onView(withText(CHECK_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "CheckCard")
    }

    @Test
    fun testSuccessfulTokenPayment() {
        onView(withId(R.id.recyclerView))
            .perform(swipeUp())

        onView(withText(TOKEN_PAYMENTS_LABEL))
            .perform(scrollTo())
            .perform(click())

        composeTestRule
            .onNodeWithText("TOKENIZE A NEW CARD")
            .assertIsEnabled()
            .performClick()

        Thread.sleep(500)

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(TokenPaymentsActivity::class.java.name) {
            composeTestRule
                .onNodeWithText("Security code")
                .assertIsEnabled()
                .performTextInput(CARD_SECURITY_CODE)

            closeSoftKeyboard()

            composeTestRule
                .onNodeWithText("PAYMENT")
                .assertIsEnabled()
                .performClick()
        }

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun testSuccessfulTokenPreauth() {
        onView(withId(R.id.recyclerView))
            .perform(swipeUp())

        onView(withText(TOKEN_PAYMENTS_LABEL))
            .perform(scrollTo())
            .perform(click())

        composeTestRule
            .onNodeWithText("TOKENIZE A NEW CARD")
            .assertIsEnabled()
            .performClick()

        Thread.sleep(500)

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(TokenPaymentsActivity::class.java.name) {
            composeTestRule
                .onNodeWithText("Security code")
                .assertIsEnabled()
                .performTextInput(CARD_SECURITY_CODE)

            closeSoftKeyboard()

            composeTestRule
                .onNodeWithText("PRE-AUTH")
                .assertIsEnabled()
                .performClick()
        }

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "PreAuth")
    }

    @Test
    fun testSuccessfulFrictionlessPayment() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
            }.commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless Successful",
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun testFrictionlessNoMethodPayment() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
            }.commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless NoMethod",
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")
    }

    @Test
    fun testFrictionlessAuthFailedPayment() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
            }.commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .check(matches(isEnabled()))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless AuthFailed",
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        awaitActivityThenRun(ResultActivity::class.java.name) {
            onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(2))
                .check(matches(hasDescendant(withText("Unable to process transaction. Card authentication failed with 3DS Server."))))
        }
    }

    @Test
    fun testSuccessfulPaymentMethodsCardPayment() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("should_payment_methods_verify_security_code", true)
            }.commit()

        onView(withText(PAYMENT_METHODS_LABEL))
            .perform(click())

        Thread.sleep(1000)

        try {
            onView(withId(R.id.addButton))
                .check(matches(isEnabled()))
                .perform(click())

            enterPaymentSheetDetails(
                CARD_NUMBER,
                CARDHOLDER_NAME,
                CARD_EXPIRY,
                CARD_SECURITY_CODE,
            )

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "Payment")
        } catch (e: NoMatchingViewException) {
            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "Payment")
        }
    }

    @Test
    fun testSuccessfulPaymentMethodsPreauth() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("should_payment_methods_verify_security_code", true)
            }.commit()

        onView(withId(R.id.recyclerView))
            .perform(swipeUp())

        onView(withText(PREAUTH_METHODS_LABEL))
            .perform(click())

        Thread.sleep(1000)

        try {
            onView(withId(R.id.addButton))
                .check(matches(isEnabled()))
                .perform(click())

            enterPaymentSheetDetails(
                CARD_NUMBER,
                CARDHOLDER_NAME,
                CARD_EXPIRY,
                CARD_SECURITY_CODE,
            )

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "PreAuth")
        } catch (e: NoMatchingViewException) {
            onView(withId(R.id.payButton))
                .perform(click())

            onView(withId(R.id.securityNumberTextInputEditText))
                .perform(clearText(), typeText(CARD_SECURITY_CODE))

            onView(withId(R.id.cardEntrySubmitButton))
                .check(matches(isEnabled()))
                .perform(click())

            clickCompleteOn3DS2Screen()

            assertReceiptObject("", "", "Success", "PreAuth")
        }
    }

    @Test
    fun testUserCanSuccessfullyRemoveCardInPaymentMethods() {
        onView(withText(PAYMENT_METHODS_LABEL))
            .perform(click())

        Thread.sleep(5000)

        doOnView(withId(R.id.addButton), click())

        enterPaymentSheetDetails(
            "4222 0000 0122 7408",
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(10000)

        onView(withId(R.id.subTitle))
            .check(matches(withText("Visa Ending 7408")))

        onView(withId(R.id.editButton))
            .check(matches(isEnabled()))
            .perform(click())

        onView(withId(R.id.removeCardIcon))
            .check(matches(isEnabled()))
            .perform(click())

        onView(withText("Delete"))
            .perform(click())

        Thread.sleep(1000)

        onView(withText("Visa Ending 7408"))
            .check(doesNotExist())
    }

    @Test
    fun testSuccessfulPaymentWithBillingDetails() {
        toggleBillingInfoSetting(true)

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1000)

        fillBillingDetails(
            VALID_EMAIL,
            VALID_COUNTRY,
            VALID_MOBILE,
            VALID_ADDRESS,
            VALID_CITY,
            VALID_POSTCODE,
        )

        onView(withId(R.id.billingDetailsSubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")

        toggleBillingInfoSetting(false)
    }

    @Test
    fun testUKPostCodeValidation() {
        toggleBillingInfoSetting(true)

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1000)

        fillBillingDetails(
            VALID_EMAIL,
            VALID_COUNTRY,
            VALID_MOBILE,
            VALID_ADDRESS,
            VALID_CITY,
            INVALID_POSTCODE,
        )

        onView(withId(R.id.cityTextInputEditText))
            .perform(click())

        onView(allOf(withId(R.id.errorTextView), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(INVALID_POSTCODE_LABEL)))

        toggleBillingInfoSetting(false)
    }

    @Test
    fun testUSPostCodeValidation() {
        toggleBillingInfoSetting(true)

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1000)

        fillBillingDetails(
            VALID_EMAIL,
            "United States",
            VALID_MOBILE,
            VALID_ADDRESS,
            VALID_CITY,
            INVALID_POSTCODE,
        )

        onView(withId(R.id.cityTextInputEditText))
            .perform(click())

        onView(allOf(withId(R.id.errorTextView), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(INVALID_ZIPCODE_LABEL)))

        toggleBillingInfoSetting(false)
    }

    @Test
    fun testCAPostCodeValidation() {
        toggleBillingInfoSetting(true)

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1000)

        fillBillingDetails(
            VALID_EMAIL,
            "Canada",
            VALID_MOBILE,
            VALID_ADDRESS,
            VALID_CITY,
            INVALID_POSTCODE,
        )

        onView(withId(R.id.cityTextInputEditText))
            .perform(click())

        onView(allOf(withId(R.id.errorTextView), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(INVALID_POSTCODE_LABEL)))

        toggleBillingInfoSetting(false)
    }

    @Test
    fun testBillingFieldsInputValidation() {
        toggleBillingInfoSetting(true)

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1000)

        fillTextField(R.id.emailTextInputEditText, SPECIAL_CHARACTERS)
        closeSoftKeyboard()
        onView(withId(R.id.cityTextInputEditText)).perform(click())
        closeSoftKeyboard()
        Thread.sleep(500)
        onView(allOf(withId(R.id.errorTextView), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(INVALID_EMAIL_LABEL)))
        if (isTablet()) {
            closeSoftKeyboard()
        }
        fillTextField(R.id.emailTextInputEditText, VALID_EMAIL)

        selectFromMultipleAndEnterText(R.id.countryTextInputEditText, VALID_COUNTRY)
        onView(withText(VALID_COUNTRY)).perform(click())
        fillTextField(R.id.mobileNumberTextInputEditText, VALID_COUNTRY_CODE)
        onView(withId(R.id.cityTextInputEditText)).perform(click())
        onView(allOf(withId(R.id.errorTextView), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(INVALID_PHONE_LABEL)))
        Thread.sleep(500)
        fillTextField(R.id.mobileNumberTextInputEditText, VALID_MOBILE)

        fillTextField(R.id.addressLine1TextInputEditText, SPECIAL_CHARACTERS)
        onView(withId(R.id.cityTextInputEditText)).perform(click())
        onView(allOf(withId(R.id.errorTextView), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(INVALID_ADDRESS_LABEL)))
        fillTextField(R.id.addressLine1TextInputEditText, VALID_ADDRESS)

        fillTextField(R.id.cityTextInputEditText, SPECIAL_CHARACTERS)
        onView(withId(R.id.addressLine1TextInputEditText)).perform(click())
        onView(allOf(withId(R.id.errorTextView), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            .check(matches(withText(INVALID_CITY_LABEL)))
        fillTextField(R.id.cityTextInputEditText, VALID_CITY)

        toggleBillingInfoSetting(false)
    }

    @Test
    fun testStepUpPaymentTransaction() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
                putString("scaExemption", "LOW_VALUE")
            }.commit()
        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless Successful",
            CARD_EXPIRY,
            WRONG_CV2,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertOnView(withText("COMPLETE"))

        clickCompleteOn3DS2Screen()

        assertReceiptObject("Card declined: CV2 policy", "", "Declined", "Payment")
    }

    @Test
    fun testStepUpPreauthTransaction() {
        sharedPrefs
            .edit()
            .apply {
                putString("challengeRequestIndicator", "NO_PREFERENCE")
                putString("scaExemption", "LOW_VALUE")
            }.commit()
        onView(withText(PREAUTH_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            "Frictionless Successful",
            CARD_EXPIRY,
            WRONG_CV2,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertOnView(withText("COMPLETE"))

        clickCompleteOn3DS2Screen()

        assertReceiptObject("Card declined: CV2 policy", "", "Declined", "PreAuth")
    }

    @Test
    fun testPrimaryAccountDetailsTransaction() {
        sharedPrefs
            .edit()
            .apply {
                putBoolean("is_primary_account_details_enabled", true)
            }.commit()

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        assertOnView(withText("COMPLETE"))

        clickCompleteOn3DS2Screen()

        assertPADTransaction()
    }

    @Test
    fun testIndiaStateField() {
        toggleBillingInfoSetting(true)

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1000)

        fillBillingDetails(
            VALID_EMAIL,
            "India",
            VALID_MOBILE,
            VALID_ADDRESS,
            VALID_CITY,
            VALID_POSTCODE,
            "Kerala",
        )

        onView(withId(R.id.billingDetailsSubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")

        toggleBillingInfoSetting(false)
    }

    @Test
    fun testChinaStateField() {
        toggleBillingInfoSetting(true)

        onView(withText(PAY_WITH_CARD_LABEL))
            .perform(click())

        enterPaymentSheetDetails(
            CARD_NUMBER,
            CARDHOLDER_NAME,
            CARD_EXPIRY,
            CARD_SECURITY_CODE,
        )

        onView(withId(R.id.cardEntrySubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1000)

        fillBillingDetails(
            VALID_EMAIL,
            "China",
            VALID_MOBILE,
            VALID_ADDRESS,
            VALID_CITY,
            VALID_POSTCODE,
            "Sichuan Sheng",
        )

        onView(withId(R.id.billingDetailsSubmitButton))
            .check(matches(isEnabled()))
            .perform(click())

        clickCompleteOn3DS2Screen()

        assertReceiptObject("AuthCode: ", "", "Success", "Payment")

        toggleBillingInfoSetting(false)
    }
}

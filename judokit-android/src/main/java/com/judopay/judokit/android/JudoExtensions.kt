@file:Suppress("TooManyFunctions")

package com.judopay.judokit.android

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.FloatRange
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.request.CheckCardRequest
import com.judopay.judokit.android.api.model.request.GooglePayRequest
import com.judopay.judokit.android.api.model.request.GooglePayWallet
import com.judopay.judokit.android.api.model.request.PaymentRequest
import com.judopay.judokit.android.api.model.request.RegisterCardRequest
import com.judopay.judokit.android.api.model.request.SaveCardRequest
import com.judopay.judokit.android.api.model.request.TokenRequest
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import com.judopay.judokit.android.model.ApiEnvironment
import com.judopay.judokit.android.model.googlepay.GooglePayAddress
import com.judopay.judokit.android.ui.common.ANIMATION_DURATION_500
import com.judopay.judokit.android.ui.common.LANDSCAPE_MIN_HEIGHT_RATIO
import com.judopay.judokit.android.ui.common.parcelable
import com.judopay.judokit.android.ui.error.JudoNotProvidedError

internal val Judo.apiBaseUrl: String
    get() = if (isSandboxed) ApiEnvironment.SANDBOX.host else ApiEnvironment.LIVE.host

internal fun requireNotNullOrEmpty(
    value: String?,
    propertyName: String,
    message: String? = null,
): String {
    when {
        value == null -> throw IllegalArgumentException(message ?: "$propertyName cannot be null")
        value.isEmpty() -> throw IllegalArgumentException(message ?: "$propertyName cannot be empty")
        else -> return value
    }
}

internal fun <T : Any> requireNotNull(
    value: T?,
    propertyName: String,
    message: String? = null,
): T {
    if (value == null) {
        throw IllegalArgumentException(message ?: "$propertyName cannot be null")
    } else {
        return value
    }
}

internal fun validateTimeout(
    timeout: Long?,
    propertyName: String,
    minTimeout: Long,
    maxTimeout: Long,
): Long =
    timeout?.let {
        require(it in minTimeout..maxTimeout) {
            "$propertyName should be greater than $minTimeout seconds and less than $maxTimeout seconds"
        }

        it
    } ?: minTimeout

@Suppress("UNCHECKED_CAST")
fun <T : View> View.parentOfType(parentType: Class<T>): T? {
    var parent = parent
    while (parent is View) {
        if (parent::class.java == parentType) {
            return parent as T
        }
        parent = parent.getParent()
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.subViewsWithType(subviewType: Class<T>): List<T> {
    val views = mutableListOf<T>()
    children.forEach {
        if (it::class.java == subviewType) {
            views.add(it as T)
        } else if (it is ViewGroup) {
            views.addAll(it.subViewsWithType(subviewType))
        }
    }
    return views
}

fun ViewGroup.inflate(
    resource: Int,
    attachToRoot: Boolean = false,
): View = LayoutInflater.from(context).inflate(resource, this, attachToRoot)

val FragmentActivity.judo: Judo
    get() =
        intent.parcelable(JUDO_OPTIONS)
            ?: throw JudoNotProvidedError()

val Fragment.judo: Judo
    get() = requireActivity().judo

val String.withWhitespacesRemoved: String
    get() = replace("\\s".toRegex(), "")

internal fun String.trimIndent(replaceNewLinesWithSpaces: Boolean): String {
    if (replaceNewLinesWithSpaces) {
        return trimIndent()
            .replace("\n", " ")
            .replace("\\s+".toRegex(), " ")
    }
    return trimIndent()
}

fun Bundle.toMap(): Map<String, String> =
    keySet()
        .mapNotNull {
            val value = getString(it)
            if (value != null) Pair(it, value) else null
        }.toMap()

fun View.animateWithAlpha(
    alpha: Float,
    duration: Long = ANIMATION_DURATION_500,
) {
    animate()
        .alpha(alpha)
        .duration = duration
}

fun View.animateWithTranslation(
    translationY: Float,
    alpha: Float,
    duration: Long = ANIMATION_DURATION_500,
) {
    animate()
        .translationY(translationY)
        .alpha(alpha)
        .duration = duration
}

fun View.dismissKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

fun Any.toJSONString(): String = Gson().toJson(this)

// Replacement of autoSizeMaxTextSize for APIs lower than 26
fun TextView.setMaxTextSize(maxTextSizeSp: Float) {
    val metrics = resources.displayMetrics
    val density = metrics.density
    val maxPx = maxTextSizeSp * density
    if (textSize > maxPx) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, maxPx)
    }
}

internal fun EditText.moveCursorToEnd() {
    post {
        setSelection(text?.length ?: 0)
    }
}

@Suppress("MagicNumber")
internal fun Window.applyDialogStyling() {
    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    requestFeature(Window.FEATURE_NO_TITLE)
    setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
    )
    setDimAmount(0.5f)
}

@Suppress("LongParameterList")
fun Judo.toPaymentRequest(
    cardNumber: String,
    expiryDate: String,
    securityCode: String,
    threeDSecureTwo: ThreeDSecureTwo?,
    mobileNumber: String,
    phoneCountryCode: String? = null,
) = PaymentRequest
    .Builder()
    .setYourPaymentReference(reference.paymentReference)
    .setAmount(amount.amount)
    .setCurrency(amount.currency.name)
    .setJudoId(judoId)
    .setYourConsumerReference(reference.consumerReference)
    .setYourPaymentMetaData(reference.metaData?.toMap())
    .setAddress(address ?: Address.Builder().build())
    .setCardNumber(cardNumber)
    .setCv2(securityCode)
    .setExpiryDate(expiryDate)
    .setPrimaryAccountDetails(primaryAccountDetails)
    .setInitialRecurringPayment(initialRecurringPayment)
    .setThreeDSecure(threeDSecureTwo)
    .setMobileNumber(mobileNumber)
    .setPhoneCountryCode(phoneCountryCode)
    .setDisableNetworkTokenisation(disableNetworkTokenisation)
    .build()

@Deprecated(
    "Register Card functionality has been deprecated and will be removed in a future version. " +
        "Please use Check Card feature instead.",
)
fun Judo.toRegisterCardRequest(
    cardNumber: String,
    expirationDate: String,
    securityCode: String,
    threeDSecureTwo: ThreeDSecureTwo?,
) = RegisterCardRequest
    .Builder()
    .setYourPaymentReference(reference.paymentReference)
    .setCurrency(amount.currency.name)
    .setJudoId(judoId)
    .setYourConsumerReference(reference.consumerReference)
    .setYourPaymentMetaData(reference.metaData?.toMap())
    .setAddress(address ?: Address.Builder().build())
    .setCardNumber(cardNumber)
    .setExpiryDate(expirationDate)
    .setCv2(securityCode)
    .setPrimaryAccountDetails(primaryAccountDetails)
    .setAmount(amount.amount)
    .setInitialRecurringPayment(initialRecurringPayment)
    .setThreeDSecure(threeDSecureTwo)
    .setDisableNetworkTokenisation(disableNetworkTokenisation)
    .build()

fun Judo.toSaveCardRequest(
    cardNumber: String,
    expirationDate: String,
    securityCode: String,
    cardHolderName: String? = null,
) = SaveCardRequest
    .Builder()
    .setYourPaymentReference(reference.paymentReference)
    .setCurrency(amount.currency.name)
    .setJudoId(judoId)
    .setYourConsumerReference(reference.consumerReference)
    .setYourPaymentMetaData(reference.metaData?.toMap())
    .setAddress(address ?: Address.Builder().build())
    .setCardNumber(cardNumber)
    .setExpiryDate(expirationDate)
    .setCardHolderName(cardHolderName)
    .setCv2(securityCode)
    .setPrimaryAccountDetails(primaryAccountDetails)
    .setDisableNetworkTokenisation(disableNetworkTokenisation)
    .build()

fun Judo.toCheckCardRequest(
    cardNumber: String,
    expirationDate: String,
    securityCode: String,
    threeDSecureTwo: ThreeDSecureTwo?,
) = CheckCardRequest
    .Builder()
    .setYourPaymentReference(reference.paymentReference)
    .setCurrency(amount.currency.name)
    .setJudoId(judoId)
    .setYourConsumerReference(reference.consumerReference)
    .setYourPaymentMetaData(reference.metaData?.toMap())
    .setAddress(address ?: Address.Builder().build())
    .setCardNumber(cardNumber)
    .setExpiryDate(expirationDate)
    .setCv2(securityCode)
    .setPrimaryAccountDetails(primaryAccountDetails)
    .setInitialRecurringPayment(initialRecurringPayment)
    .setThreeDSecure(threeDSecureTwo)
    .setDisableNetworkTokenisation(disableNetworkTokenisation)
    .build()

fun Judo.toGooglePayRequest(
    cardNetwork: String,
    cardDetails: String,
    token: String,
    billingAddress: GooglePayAddress? = null,
): GooglePayRequest {
    val wallet =
        GooglePayWallet
            .Builder()
            .setToken(token)
            .setCardNetwork(cardNetwork)
            .setCardDetails(cardDetails)
            .setBillingAddress(billingAddress)
            .build()

    return GooglePayRequest
        .Builder()
        .setJudoId(judoId)
        .setAmount(amount.amount)
        .setCurrency(amount.currency.name)
        .setYourPaymentReference(reference.paymentReference)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setPrimaryAccountDetails(primaryAccountDetails)
        .setCardAddress(address)
        .setGooglePayWallet(wallet)
        .build()
}

fun Judo.toTokenRequest(
    cardToken: String,
    threeDSecureTwo: ThreeDSecureTwo? = null,
    securityCode: String? = null,
) = TokenRequest
    .Builder()
    .setAmount(amount.amount)
    .setCurrency(amount.currency.name)
    .setJudoId(judoId)
    .setYourPaymentReference(reference.paymentReference)
    .setYourConsumerReference(reference.consumerReference)
    .setYourPaymentMetaData(reference.metaData?.toMap())
    .setCardToken(cardToken)
    .setCv2(securityCode)
    .setPrimaryAccountDetails(primaryAccountDetails)
    .setAddress(address ?: Address.Builder().build())
    .setInitialRecurringPayment(initialRecurringPayment)
    .setThreeDSecure(threeDSecureTwo)
    .setDisableNetworkTokenisation(disableNetworkTokenisation)
    .build()

internal fun NestedScrollView.smoothScrollToView(targetView: View) {
    val childRect = Rect()
    targetView.getDrawingRect(childRect)
    offsetDescendantRectToMyCoords(targetView, childRect)

    val scrollY = childRect.top - (height / 2 - targetView.height / 2)
    smoothScrollTo(0, scrollY)
}

internal fun ViewGroup.getDeepChildOffset(
    mainParent: ViewGroup,
    parent: ViewParent,
    child: View,
    accumulatedOffset: Point,
) {
    val parentGroup = parent as ViewGroup
    accumulatedOffset.x += child.left
    accumulatedOffset.y += child.top

    if (parentGroup == mainParent) {
        return
    }

    getDeepChildOffset(mainParent, parentGroup.parent, parentGroup, accumulatedOffset)
}

internal fun CollapsingToolbarLayout.setAdaptiveMinHeight(
    @FloatRange(from = 0.0, to = 1.0) landscapeMinHeightPercentage: Float = LANDSCAPE_MIN_HEIGHT_RATIO,
    @DimenRes portraitMinHeightRes: Int = R.dimen.app_bar_layout_min_height,
) {
    minimumHeight =
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            (resources.displayMetrics.heightPixels * landscapeMinHeightPercentage).toInt()
        } else {
            resources.getDimensionPixelSize(portraitMinHeightRes)
        }
}

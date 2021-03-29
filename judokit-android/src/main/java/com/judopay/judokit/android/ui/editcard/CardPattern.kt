package com.judopay.judokit.android.ui.editcard

import android.content.Context
import androidx.core.content.ContextCompat
import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.iconImageResId

enum class CardPattern {
    BLACK,
    TWILIGHT_BLUE,
    DARKISH_GREEN,
    ROUGE,
    TERRA_COTTA,
    YELLOWISH_ORANGE,
    BLUISH_GREY,
    MOSS
}

internal fun CardPattern.colorRes(context: Context) = when (this) {
    CardPattern.BLACK -> ContextCompat.getColor(context, R.color.black)
    CardPattern.TWILIGHT_BLUE -> ContextCompat.getColor(context, R.color.twilight_blue)
    CardPattern.DARKISH_GREEN -> ContextCompat.getColor(context, R.color.darkish_green)
    CardPattern.ROUGE -> ContextCompat.getColor(context, R.color.rouge)
    CardPattern.TERRA_COTTA -> ContextCompat.getColor(context, R.color.terra_cotta)
    CardPattern.YELLOWISH_ORANGE -> ContextCompat.getColor(context, R.color.yellowish_orange)
    CardPattern.BLUISH_GREY -> ContextCompat.getColor(context, R.color.bluish_grey)
    CardPattern.MOSS -> ContextCompat.getColor(context, R.color.moss)
}

internal fun CardPattern.drawableRes(context: Context) = when (this) {
    CardPattern.BLACK -> ContextCompat.getDrawable(context, R.drawable.card_black_background)
    CardPattern.TWILIGHT_BLUE -> ContextCompat.getDrawable(context, R.drawable.card_twilight_blue_background)
    CardPattern.DARKISH_GREEN -> ContextCompat.getDrawable(context, R.drawable.card_darkish_green_background)
    CardPattern.ROUGE -> ContextCompat.getDrawable(context, R.drawable.card_rouge_background)
    CardPattern.TERRA_COTTA -> ContextCompat.getDrawable(context, R.drawable.card_terra_cotta_background)
    CardPattern.YELLOWISH_ORANGE -> ContextCompat.getDrawable(context, R.drawable.card_yellowish_green_background)
    CardPattern.BLUISH_GREY -> ContextCompat.getDrawable(context, R.drawable.card_bluish_grey_background)
    CardPattern.MOSS -> ContextCompat.getDrawable(context, R.drawable.card_moss_background)
}

internal fun CardPattern.cardIcon(cardNetwork: CardNetwork): Int = when (cardNetwork) {
    CardNetwork.VISA -> when (this) {
        CardPattern.BLACK,
        CardPattern.TWILIGHT_BLUE -> R.drawable.ic_card_visa_light
        else -> R.drawable.ic_card_visa
    }
    else -> cardNetwork.iconImageResId
}

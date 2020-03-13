package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.CardNetwork
import com.judopay.ui.common.isExpired
import com.judopay.ui.paymentmethods.model.CardViewModel
import com.judopay.ui.paymentmethods.model.CardViewType
import kotlinx.android.synthetic.main.payment_card_view.view.cardNameTextView
import kotlinx.android.synthetic.main.payment_card_view.view.cardNumberMaskTextView
import kotlinx.android.synthetic.main.payment_card_view.view.expireDateTextView
import kotlinx.android.synthetic.main.payment_card_view.view.isExpiredTextView
import kotlinx.android.synthetic.main.payment_card_view.view.paymentCardViewContainer

data class PaymentCardViewModel(
    override val type: CardViewType = CardViewType.CARD,
    override var layoutId: Int = R.id.paymentCardView,
    val id: Int = 0,
    val cardNetwork: CardNetwork = CardNetwork.VISA,
    val name: String = "",
    val maskedNumber: String = "",
    val expireDate: String = ""
) : CardViewModel{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentCardViewModel

        if (type != other.type) return false
        if (id != other.id) return false
        if (cardNetwork != other.cardNetwork) return false
        if (name != other.name) return false
        if (maskedNumber != other.maskedNumber) return false
        if (expireDate != other.expireDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + id
        result = 31 * result + cardNetwork.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + maskedNumber.hashCode()
        result = 31 * result + expireDate.hashCode()
        return result
    }
}

class PaymentCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    init {
        inflate(R.layout.payment_card_view, true)
    }

    var model = PaymentCardViewModel()
        set(value) {
            field = value
            update()
        }

    private fun update() {
        cardNameTextView.text = model.name
        cardNumberMaskTextView.text = resources.getString(R.string.mask, model.maskedNumber)
        expireDateTextView.text = model.expireDate
        if (isExpired(model.expireDate)) {
            isExpiredTextView.visibility = View.VISIBLE
            expireDateTextView.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
            paymentCardViewContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.greyish))
        }
    }

}

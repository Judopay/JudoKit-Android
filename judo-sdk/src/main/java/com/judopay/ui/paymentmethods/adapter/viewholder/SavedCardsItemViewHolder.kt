package com.judopay.ui.paymentmethods.adapter.viewholder

import android.text.Spanned
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.judopay.R
import com.judopay.model.displayName
import com.judopay.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import kotlinx.android.synthetic.main.saved_card_item.view.radioIconImageView
import kotlinx.android.synthetic.main.saved_card_item.view.subTitle
import kotlinx.android.synthetic.main.saved_card_item.view.title
import java.text.SimpleDateFormat
import java.util.*

class SavedCardsItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<PaymentMethodSavedCardItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodSavedCardItem, listener: PaymentMethodsAdapterListener?) =
        with(itemView) {
            subTitle.text = getSubtitleByExpiryDate(model)
            title.text = model.title
            val checkMark = if (model.isSelected) R.drawable.ic_radio_on else R.drawable.ic_radio_off
            radioIconImageView.setImageResource(checkMark)

            setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
        }

    private fun getSubtitleByExpiryDate(model: PaymentMethodSavedCardItem): Spanned {
        val today = Date()
        val expiryDate = SimpleDateFormat("MM/yy", Locale.UK).parse(model.expireDate) ?: today
        val twoMonths = Calendar.getInstance().apply { add(Calendar.MONTH, 2) }.time
        with(itemView) {
            return when {
                expiryDate.before(today) -> {
                    subTitle.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
                    HtmlCompat.fromHtml(
                        resources.getString(
                            R.string.card_subtitle_expired,
                            model.network.displayName,
                            model.ending
                        ), HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
                expiryDate.after(today) && expiryDate.before(twoMonths) -> {
                    HtmlCompat.fromHtml(
                        resources.getString(
                            R.string.card_subtitle_will_expire_soon,
                            model.network.displayName,
                            model.ending
                        ), HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
                else -> HtmlCompat.fromHtml(
                    resources.getString(
                        R.string.card_subtitle,
                        model.network.displayName,
                        model.ending
                    ), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
    }
}

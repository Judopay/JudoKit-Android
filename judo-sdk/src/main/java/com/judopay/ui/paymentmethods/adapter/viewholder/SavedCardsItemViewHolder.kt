package com.judopay.ui.paymentmethods.adapter.viewholder

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.judopay.R
import com.judopay.model.displayName
import com.judopay.ui.common.isExpired
import com.judopay.ui.common.isExpiredInTwoMonths
import com.judopay.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import kotlinx.android.synthetic.main.saved_card_item.view.radioIconImageView
import kotlinx.android.synthetic.main.saved_card_item.view.subTitle
import kotlinx.android.synthetic.main.saved_card_item.view.title

class SavedCardsItemViewHolder(view: View) : RecyclerView.ViewHolder(view), BindableRecyclerViewHolder<PaymentMethodSavedCardItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodSavedCardItem, listener: PaymentMethodsAdapterListener?) = with(itemView) {

        subTitle.text = createBoldSubtitle(model)
        title.text = model.title
        val checkMark = if (model.isSelected) R.drawable.ic_radio_on else R.drawable.ic_radio_off
        radioIconImageView.setImageResource(checkMark)

        setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
    }

    private fun createBoldSubtitle(model: PaymentMethodSavedCardItem): SpannableStringBuilder {
        val boldString = SpannableStringBuilder()
        with(itemView) {
            val cardSubtitle = SpannableStringBuilder(
                context.getString(
                    R.string.card_subtitle,
                    model.network.displayName
                )
            )
            when {
                isExpired(model.expireDate) -> {
                    subTitle.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
                    boldString.apply {
                        append("${model.ending} ${resources.getString(R.string.is_expired)}")
                        val expiredIndex =
                            if (boldString.indexOf(resources.getString(R.string.expired), ignoreCase = true) == -1) {
                                0
                            } else {
                                boldString.indexOf(resources.getString(R.string.expired), ignoreCase = true)
                            }
                        setSpan(StyleSpan(Typeface.BOLD), 0, boldString.length - resources.getString(R.string.is_expired).length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        setSpan(StyleSpan(Typeface.BOLD), expiredIndex, boldString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }
                isExpiredInTwoMonths(model.expireDate) -> {
                    boldString.apply {
                        append("${model.ending} ${resources.getString(R.string.will_expire_soon)}")
                        val expireIndex =
                            if (boldString.indexOf(resources.getString(R.string.expire_soon), ignoreCase = true) == -1) {
                                0
                            } else {
                                boldString.indexOf(resources.getString(R.string.expire_soon), ignoreCase = true)
                            }
                        setSpan(StyleSpan(Typeface.BOLD), 0, boldString.length - resources.getString(R.string.will_expire_soon).length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        setSpan(StyleSpan(Typeface.BOLD), expireIndex, boldString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                    subTitle.setTextColor(ContextCompat.getColor(context, R.color.warm_grey))
                }
                else -> boldString.apply {
                    append(model.ending)
                    setSpan(StyleSpan(Typeface.BOLD), 0, boldString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    subTitle.setTextColor(ContextCompat.getColor(context, R.color.warm_grey))
                }
            }

            cardSubtitle.append(" ")
            cardSubtitle.append(boldString)
            return cardSubtitle
        }
    }
}

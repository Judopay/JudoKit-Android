package com.judokit.android.ui.paymentmethods.adapter.viewholder

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.R
import com.judokit.android.api.model.response.CardDate
import com.judokit.android.model.displayName
import com.judokit.android.model.iconImageResId
import com.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import kotlinx.android.synthetic.main.saved_card_item.view.arrowIcon
import kotlinx.android.synthetic.main.saved_card_item.view.networkIconContainer
import kotlinx.android.synthetic.main.saved_card_item.view.networkIconImageView
import kotlinx.android.synthetic.main.saved_card_item.view.radioIconImageView
import kotlinx.android.synthetic.main.saved_card_item.view.removeCardIcon
import kotlinx.android.synthetic.main.saved_card_item.view.subTitle
import kotlinx.android.synthetic.main.saved_card_item.view.title

class SavedCardsItemViewHolder(view: View) :
    RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<PaymentMethodSavedCardItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodSavedCardItem, listener: PaymentMethodsAdapterListener?) =
        with(itemView) {
            val params = networkIconContainer.layoutParams as ViewGroup.MarginLayoutParams
            if (model.isInEditMode) {
                removeCardIcon.visibility = View.VISIBLE
                arrowIcon.visibility = View.VISIBLE
                radioIconImageView.visibility = View.GONE
                params.setMargins(resources.getDimension(R.dimen.space_24).toInt(), 0, 0, 0)
                networkIconContainer.layoutParams = params
                removeCardIcon.setOnClickListener {
                    listener?.invoke(
                        PaymentMethodItemAction.DELETE_CARD,
                        model
                    )
                }
                setOnClickListener { listener?.invoke(PaymentMethodItemAction.EDIT_CARD, model) }
            } else {
                removeCardIcon.visibility = View.GONE
                arrowIcon.visibility = View.GONE
                params.setMargins(0, 0, 0, 0)
                networkIconContainer.layoutParams = params
                val checkMark =
                    if (model.isSelected) R.drawable.ic_radio_on else R.drawable.ic_radio_off
                radioIconImageView.visibility = View.VISIBLE
                radioIconImageView.setImageResource(checkMark)
                setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
            }
            subTitle.text = createBoldSubtitle(model)
            title.text = model.title

            val image = model.network.iconImageResId
            if (image > 0) {
                networkIconImageView.setImageResource(image)
            } else {
                networkIconImageView.setImageDrawable(null)
            }
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
            val date = CardDate(model.expireDate)
            when {
                !date.isAfterToday -> {
                    subTitle.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
                    boldString.apply {
                        append("${model.ending} ${resources.getString(R.string.is_expired)}")
                        val expiredIndex =
                            if (boldString.indexOf(
                                resources.getString(R.string.expired),
                                ignoreCase = true
                            ) == -1
                            ) {
                                0
                            } else {
                                boldString.indexOf(
                                    resources.getString(R.string.expired),
                                    ignoreCase = true
                                )
                            }
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            boldString.length - resources.getString(R.string.is_expired).length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            expiredIndex,
                            boldString.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                }
                date.isExpiredInTwoMonths -> {
                    boldString.apply {
                        append("${model.ending} ${resources.getString(R.string.will_expire_soon)}")
                        val expireIndex =
                            if (boldString.indexOf(
                                resources.getString(R.string.expire_soon),
                                ignoreCase = true
                            ) == -1
                            ) {
                                0
                            } else {
                                boldString.indexOf(
                                    resources.getString(R.string.expire_soon),
                                    ignoreCase = true
                                )
                            }
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            boldString.length - resources.getString(R.string.will_expire_soon).length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            expireIndex,
                            boldString.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                    subTitle.setTextColor(ContextCompat.getColor(context, R.color.warm_grey))
                }
                else -> boldString.apply {
                    append(model.ending)
                    setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        boldString.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    subTitle.setTextColor(ContextCompat.getColor(context, R.color.warm_grey))
                }
            }

            cardSubtitle.append(" ")
            cardSubtitle.append(boldString)
            return cardSubtitle
        }
    }
}

package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.databinding.SavedCardItemBinding
import com.judopay.judokit.android.model.displayName
import com.judopay.judokit.android.model.iconImageResId
import com.judopay.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem

class SavedCardsItemViewHolder(private val binding: SavedCardItemBinding) :
    RecyclerView.ViewHolder(binding.root), BindableRecyclerViewHolder<PaymentMethodSavedCardItem, PaymentMethodItemAction> {
    override fun bind(
        model: PaymentMethodSavedCardItem,
        listener: PaymentMethodsAdapterListener?,
    ) = with(binding.root) {
        val params = binding.networkIconContainer.layoutParams as ViewGroup.MarginLayoutParams
        if (model.isInEditMode) {
            binding.removeCardIcon.visibility = View.VISIBLE
            binding.arrowIcon.visibility = View.VISIBLE
            binding.radioIconImageView.visibility = View.GONE
            params.setMargins(resources.getDimension(R.dimen.space_24).toInt(), 0, 0, 0)
            binding.networkIconContainer.layoutParams = params
            binding.removeCardIcon.setOnClickListener {
                listener?.invoke(
                    PaymentMethodItemAction.DELETE_CARD,
                    model,
                )
            }
            setOnClickListener { listener?.invoke(PaymentMethodItemAction.EDIT_CARD, model) }
        } else {
            binding.removeCardIcon.visibility = View.GONE
            binding.arrowIcon.visibility = View.GONE
            params.setMargins(0, 0, 0, 0)
            binding.networkIconContainer.layoutParams = params
            val checkMark =
                if (model.isSelected) R.drawable.ic_radio_on else R.drawable.ic_radio_off
            binding.radioIconImageView.tag = model.isSelected.toString()
            binding.radioIconImageView.visibility = View.VISIBLE
            binding.radioIconImageView.setImageResource(checkMark)
            setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
        }
        binding.subTitle.text = createBoldSubtitle(model)
        binding.title.text = model.title

        val image = model.network.iconImageResId
        if (image > 0) {
            binding.networkIconImageView.setImageResource(image)
        } else {
            binding.networkIconImageView.setImageDrawable(null)
        }
    }

    @Suppress("LongMethod", "NestedBlockDepth")
    private fun createBoldSubtitle(model: PaymentMethodSavedCardItem): SpannableStringBuilder {
        val boldString = SpannableStringBuilder()
        with(itemView) {
            val cardSubtitle =
                SpannableStringBuilder(
                    context.getString(
                        R.string.jp_card_subtitle,
                        model.network.displayName,
                    ),
                )
            val date = CardDate(model.expireDate)
            when {
                !date.isAfterToday -> {
                    binding.subTitle.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
                    boldString.apply {
                        append("${model.ending} ${resources.getString(R.string.jp_is_expired)}")
                        val expiredIndex =
                            if (boldString.indexOf(
                                    resources.getString(R.string.jp_expired),
                                    ignoreCase = true,
                                ) == -1
                            ) {
                                0
                            } else {
                                boldString.indexOf(
                                    resources.getString(R.string.jp_expired),
                                    ignoreCase = true,
                                )
                            }
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            boldString.length - resources.getString(R.string.jp_is_expired).length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE,
                        )
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            expiredIndex,
                            boldString.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE,
                        )
                    }
                }
                date.isExpiredInTwoMonths -> {
                    boldString.apply {
                        append("${model.ending} ${resources.getString(R.string.jp_will_expire_soon)}")
                        val expireIndex =
                            if (boldString.indexOf(
                                    resources.getString(R.string.jp_expire_soon),
                                    ignoreCase = true,
                                ) == -1
                            ) {
                                0
                            } else {
                                boldString.indexOf(
                                    resources.getString(R.string.jp_expire_soon),
                                    ignoreCase = true,
                                )
                            }
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            boldString.length - resources.getString(R.string.jp_will_expire_soon).length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE,
                        )
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            expireIndex,
                            boldString.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE,
                        )
                    }
                    binding.subTitle.setTextColor(ContextCompat.getColor(context, R.color.warm_grey))
                }
                else ->
                    boldString.apply {
                        append(model.ending)
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            boldString.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE,
                        )
                        binding.subTitle.setTextColor(ContextCompat.getColor(context, R.color.warm_grey))
                    }
            }

            cardSubtitle.append(" ")
            cardSubtitle.append(boldString)
            return cardSubtitle
        }
    }
}

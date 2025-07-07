package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.AdministrativeDivision
import java.text.Normalizer

class DiacriticInsensitiveAdapter(
    context: Context,
    private val items: List<AdministrativeDivision>,
) : ArrayAdapter<AdministrativeDivision>(context, R.layout.country_select_dialog_item, items) {
    private val filteredItems =
        items.toMutableList()

    override fun getCount() = filteredItems.size

    override fun getItem(position: Int) = filteredItems[position]

    override fun getFilter(): Filter =
        object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query =
                    constraint?.let { normalize(it.toString()) } ?: ""
                val filteredList =
                    if (query.isEmpty()) {
                        items
                    } else {
                        items.filter {
                            normalize(it.name).contains(query, ignoreCase = true)
                        }
                    }
                return FilterResults().apply {
                    values = filteredList
                    count = filteredList.size
                }
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?,
            ) {
                if (results?.values is List<*>) {
                    filteredItems.clear()
                    filteredItems.addAll(results.values as List<AdministrativeDivision>)
                    notifyDataSetChanged()
                }
            }
        }

    private fun normalize(text: String): String =
        Normalizer
            .normalize(text, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
}

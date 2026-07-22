package com.judokit.android.examples.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object SettingsImporter {
    // this is for the sole purpose of having a better organized JSON
    private val SECTIONS: List<Pair<String, List<String>>> =
        listOf(
            "api" to
                listOf(
                    "is_sandboxed",
                    "is_using_fabrick_3ds_service",
                    "judo_id",
                    "token",
                    "secret",
                    "is_payment_session_enabled",
                    "payment_session",
                    "payment_reference",
                ),
            "recommendation" to
                listOf(
                    "is_recommendation_enabled",
                    "recommendation_url",
                    "rsa_key",
                    "recommendation_timeout",
                    "is_recommendation_halt_transaction_enabled",
                ),
            "three_ds" to
                listOf(
                    "should_ask_for_billing_information",
                    "challenge_request_indicator",
                    "sca_exemption",
                    "three_ds_two_max_timeout",
                    "connect_timeout",
                    "read_timeout",
                    "write_timeout",
                    "three_ds_two_message_version",
                ),
            "three_ds_ui_customisation" to
                listOf(
                    "three_ds_is_ui_customisation_enabled",
                    "three_ds_toolbar_text_font_name",
                    "three_ds_toolbar_text_color",
                    "three_ds_toolbar_text_font_size",
                    "three_ds_toolbar_background_color",
                    "three_ds_toolbar_header_text",
                    "three_ds_toolbar_button_text",
                    "three_ds_label_text_font_name",
                    "three_ds_label_text_color",
                    "three_ds_label_text_font_size",
                    "three_ds_label_heading_text_font_name",
                    "three_ds_label_heading_text_color",
                    "three_ds_label_heading_text_font_size",
                    "three_ds_text_box_text_font_name",
                    "three_ds_text_box_text_color",
                    "three_ds_text_box_text_font_size",
                    "three_ds_text_box_border_width",
                    "three_ds_text_box_border_color",
                    "three_ds_text_box_corner_radius",
                    "three_ds_submit_button_text_font_name",
                    "three_ds_submit_button_text_color",
                    "three_ds_submit_button_text_font_size",
                    "three_ds_submit_button_background_color",
                    "three_ds_submit_button_corner_radius",
                    "three_ds_next_button_text_font_name",
                    "three_ds_next_button_text_color",
                    "three_ds_next_button_text_font_size",
                    "three_ds_next_button_background_color",
                    "three_ds_next_button_corner_radius",
                    "three_ds_continue_button_text_font_name",
                    "three_ds_continue_button_text_color",
                    "three_ds_continue_button_text_font_size",
                    "three_ds_continue_button_background_color",
                    "three_ds_continue_button_corner_radius",
                    "three_ds_cancel_button_text_font_name",
                    "three_ds_cancel_button_text_color",
                    "three_ds_cancel_button_text_font_size",
                    "three_ds_cancel_button_background_color",
                    "three_ds_cancel_button_corner_radius",
                    "three_ds_resend_button_text_font_name",
                    "three_ds_resend_button_text_color",
                    "three_ds_resend_button_text_font_size",
                    "three_ds_resend_button_background_color",
                    "three_ds_resend_button_corner_radius",
                ),
            "amount" to
                listOf(
                    "amount",
                    "currency",
                ),
            "address" to
                listOf(
                    "is_address_enabled",
                    "address_line_1",
                    "address_line_2",
                    "address_line_3",
                    "address_town",
                    "address_post_code",
                    "address_billing_country",
                    "address_country_code",
                    "address_administrative_division",
                    "address_phone_country_code",
                    "address_mobile_number",
                    "address_email_address",
                ),
            "primary_account" to
                listOf(
                    "is_primary_account_details_enabled",
                    "primary_account_name",
                    "primary_account_account_number",
                    "primary_account_date_of_birth",
                    "primary_account_post_code",
                ),
            "google_pay" to
                listOf(
                    "is_google_pay_production_environment",
                    "google_pay_merchant_name",
                    "google_pay_country_code",
                    "billing_address",
                    "is_billing_address_phone_number_required",
                    "is_shipping_address_required",
                    "google_pay_shipping_address_allowed_countries",
                    "is_shipping_address_phone_number_required",
                    "is_email_address_required",
                    "allow_prepaid_cards",
                    "allow_credit_cards",
                    "google_pay_transaction_id",
                    "google_pay_total_price_status",
                    "google_pay_total_price_label",
                    "google_pay_checkout_option",
                    "google_pay_mit_type",
                    "google_pay_mit_token_update_url",
                    "google_pay_mit_management_url",
                    "google_pay_mit_billing_agreement",
                    "google_pay_mit_immediate_total_price",
                    "google_pay_automatic_reload_minimum_balance",
                    "google_pay_automatic_reload_amount",
                    "google_pay_automatic_reload_label",
                    "google_pay_deferred_billing_date_time",
                    "google_pay_deferred_price_status",
                    "google_pay_deferred_price",
                    "google_pay_deferred_label",
                    "google_pay_recurring_item_label",
                    "google_pay_recurring_item_price_status",
                    "google_pay_recurring_item_price",
                    "google_pay_recurring_item_period",
                    "google_pay_recurring_item_period_count",
                    "google_pay_recurring_item_billing_initial_date_time",
                    "google_pay_recurring_item_billing_final_date_time",
                    "is_google_pay_recurring_introductory_period_enabled",
                    "google_pay_recurring_introductory_period_end_date_time",
                    "google_pay_recurring_introductory_period_label",
                    "google_pay_recurring_introductory_period_total_price",
                ),
            "others" to
                listOf(
                    "is_avs_enabled",
                    "should_payment_methods_verify_security_code",
                    "should_payment_methods_display_amount",
                    "should_payment_button_display_amount",
                    "is_initial_recurring_payment",
                    "is_delayed_authorisation_on",
                    "is_allow_increment_on",
                    "supported_networks",
                    "payment_methods",
                    "is_disable_network_tokenisation_on",
                ),
            "token_payments" to
                listOf(
                    "should_ask_for_csc",
                    "should_ask_for_cardholder_name",
                ),
        )

    fun export(prefs: SharedPreferences): String {
        val keyToSection =
            buildMap<String, String> {
                SECTIONS.forEach { (section, keys) -> keys.forEach { put(it, section) } }
            }

        val sectionObjects = mutableMapOf<String, JsonObject>()
        val uncategorized = JsonObject()

        prefs.all.entries.sortedBy { it.key }.forEach { (key, value) ->
            val section = keyToSection[key]
            val target = if (section != null) sectionObjects.getOrPut(section) { JsonObject() } else uncategorized
            addValue(target, key, value)
        }

        val root = JsonObject()
        SECTIONS.forEach { (section, _) -> sectionObjects[section]?.let { root.add(section, it) } }
        uncategorized.entrySet().forEach { (key, value) -> root.add(key, value) }

        return GsonBuilder().setPrettyPrinting().create().toJson(root)
    }

    private fun addValue(
        obj: JsonObject,
        key: String,
        value: Any?,
    ) {
        when (value) {
            is Boolean -> obj.addProperty(key, value)
            is String -> obj.addProperty(key, value)
            is Set<*> -> {
                val array = JsonArray()
                @Suppress("UNCHECKED_CAST")
                (value as Set<String>).sorted().forEach { array.add(it) }
                obj.add(key, array)
            }
            is Int -> obj.addProperty(key, value)
            is Long -> obj.addProperty(key, value)
            is Float -> obj.addProperty(key, value)
        }
    }

    fun import(
        prefs: SharedPreferences,
        json: String,
    ) {
        val element = JsonParser.parseString(json)
        require(element.isJsonObject) { "Expected a JSON object at the root" }
        prefs.edit {
            element.asJsonObject.entrySet().forEach { (key, child) ->
                when {
                    child.isJsonObject -> child.asJsonObject.entrySet().forEach { (k, v) -> applyLeaf(this, k, v) }
                    else -> applyLeaf(this, key, child)
                }
            }
        }
    }

    private fun applyLeaf(
        editor: SharedPreferences.Editor,
        key: String,
        element: JsonElement,
    ) {
        when {
            element.isJsonArray ->
                editor.putStringSet(key, element.asJsonArray.mapTo(mutableSetOf()) { it.asString })
            element.isJsonPrimitive -> {
                val p = element.asJsonPrimitive
                if (p.isBoolean) editor.putBoolean(key, p.asBoolean) else editor.putString(key, p.asString)
            }
        }
    }
}

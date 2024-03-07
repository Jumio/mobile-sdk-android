package com.jumio.sample.kotlin.customui.adapter

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.jumio.sample.R
import com.jumio.sdk.consent.JumioConsentItem
import com.jumio.sdk.enums.JumioConsentType

class CustomConsentAdapter(
	private val consentItems: List<JumioConsentItem>,
	private val onCheckedChange: (consentItem: JumioConsentItem, decision: Boolean) -> Unit
) : RecyclerView.Adapter<CustomConsentAdapter.ConsentViewHolder>() {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsentViewHolder =
		ConsentViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.activity_customui_consent_item, parent, false),
			onCheckedChange
		)

	override fun onBindViewHolder(holder: ConsentViewHolder, position: Int) = holder.bind(
		consentItems[position]
	)

	override fun getItemCount() = consentItems.size
	
	class ConsentViewHolder(
		itemView: View,
		private val onCheckedChange: (consentItem: JumioConsentItem, decision: Boolean) -> Unit
	) : RecyclerView.ViewHolder(itemView) {
		private var consentText: TextView = itemView.findViewById(R.id.tv_consent_item)
		var consentSwitch: SwitchCompat = itemView.findViewById(R.id.switch_consent_item)

		fun bind(consentItem: JumioConsentItem?) {
			showConsent(consentText, consentSwitch, consentItem)
		}
		private fun showConsent(
			textView: TextView,
			switch: SwitchCompat,
			consentItem: JumioConsentItem?
		) {
			val linkColor = MaterialColors.getColor(itemView, com.jumio.defaultui.R.attr.colorPrimary)
			val spannedText = consentItem?.spannedTextWithLinkColor(linkColor)
			if (spannedText.isNullOrEmpty()) {
				textView.visibility = View.GONE
				switch.visibility = View.GONE
			} else {
				textView.apply {
					text = spannedText
					movementMethod = LinkMovementMethod.getInstance()
					visibility = View.VISIBLE
				}
				switch.visibility = if (consentItem.type == JumioConsentType.ACTIVE) {
					switch.setOnCheckedChangeListener { _, isChecked ->
						onCheckedChange(consentItem, isChecked)
					}
					View.VISIBLE
				} else {
					View.GONE
				}
			}
		}
	}
}

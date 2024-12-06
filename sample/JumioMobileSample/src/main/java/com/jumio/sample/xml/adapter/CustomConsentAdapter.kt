// Copyright 2023 Jumio Corporation, all rights reserved.
package com.jumio.sample.xml.adapter

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
) : RecyclerView.Adapter<CustomConsentAdapter.ConsentViewHolder>() {

	private val activeConsentItems: List<JumioConsentItem>
		get() = consentItems.filter { it.type == JumioConsentType.ACTIVE }.toList()

	private val consentDecisions = mutableMapOf<JumioConsentItem?, Boolean>().apply {
		consentItems.forEach {
			put(it, false)
		}
	}

	private fun setConsentForItem(position: Int, userDecision: Boolean) {
		consentDecisions[consentItems.elementAt(position)] = userDecision
	}

	fun getConsentForItem(consentItem: JumioConsentItem?): Boolean = if (consentItem != null) {
		consentDecisions[consentItem] ?: false
	} else {
		false
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsentViewHolder {
		val context = parent.context
		val inflater = LayoutInflater.from(context)
		val view = inflater.inflate(R.layout.activity_customui_consent_item, parent, false)
		return ConsentViewHolder(view)
	}

	override fun onBindViewHolder(holder: ConsentViewHolder, position: Int) {
		holder.bind(consentItems[position])
		if (activeConsentItems.isNotEmpty()) {
			holder.consentSwitch.setOnCheckedChangeListener { _, isChecked ->
				setConsentForItem(position, isChecked)
			}
		}
	}

	override fun getItemCount() = consentItems.size

	class ConsentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var consentText: TextView = itemView.findViewById(R.id.tv_consent_item)
		var consentSwitch: SwitchCompat = itemView.findViewById(R.id.switch_consent_item)

		fun bind(consentItem: JumioConsentItem?) {
			showConsent(consentText, consentSwitch, consentItem)
		}

		private fun showConsent(textView: TextView, switch: SwitchCompat, consentItem: JumioConsentItem?) {
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
					View.VISIBLE
				} else {
					View.GONE
				}
			}
		}
	}
}

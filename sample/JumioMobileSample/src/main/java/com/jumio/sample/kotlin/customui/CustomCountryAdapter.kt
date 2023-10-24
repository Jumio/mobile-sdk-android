// Copyright 2022 Jumio Corporation, all rights reserved.
package com.jumio.sample.kotlin.customui

import android.content.Context
import android.widget.ArrayAdapter
import com.jumio.sdk.document.JumioDocument

/**
 * Array adapter implementation for the country list spinner
 */
class CustomCountryAdapter(context: Context, private val countryList: Map<String, List<JumioDocument>>) :
	ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

	init {
		addAll(countryList.keys.sorted())
	}

	/**
	 * Return a list of [JumioDocument] for the current position
	 *
	 * @param position
	 * @return List of [JumioDocument]
	 */
	fun getDocumentList(position: Int): List<JumioDocument> = countryList[getItem(position)] ?: emptyList()
}
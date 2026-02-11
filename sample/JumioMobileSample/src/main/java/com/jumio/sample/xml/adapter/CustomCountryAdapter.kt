// (c) 2026 Jumio All rights reserved. US Patent App.
package com.jumio.sample.xml.adapter

import android.content.Context
import android.widget.ArrayAdapter

class CustomCountryAdapter(
	context: Context,
	countryList: List<String>,
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

	init {
		val sortedCountryList = ArrayList(countryList).apply { sort() }
		addAll(sortedCountryList)
	}
}

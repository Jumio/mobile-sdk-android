// Copyright 2023 Jumio Corporation, all rights reserved.
package com.jumio.sample.customui.adapter

import android.content.Context
import android.widget.ArrayAdapter

class CustomCountryAdapter(
	context: Context,
	countryList: List<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

	init {
		val sortedCountryList = ArrayList(countryList).apply { sort() }
		addAll(sortedCountryList)
	}
}

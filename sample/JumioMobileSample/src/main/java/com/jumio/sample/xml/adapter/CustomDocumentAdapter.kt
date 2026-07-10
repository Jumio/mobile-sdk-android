// (c) 2026 Jumio All rights reserved. US Patent App.
package com.jumio.sample.xml.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.jumio.sample.compose.extension.getName
import com.jumio.sdk.document.JumioDocument

class CustomDocumentAdapter(
	context: Context,
	private val documents: List<JumioDocument>,
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

	init {
		documents.forEach {
			add(it.getName())
		}
	}

	fun getDocument(position: Int): JumioDocument {
		return documents.elementAt(position)
	}
}

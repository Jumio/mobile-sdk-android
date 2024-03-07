package com.jumio.sample.kotlin.customui.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.jumio.sdk.document.JumioDigitalDocument
import com.jumio.sdk.document.JumioDocument
import com.jumio.sdk.document.JumioPhysicalDocument

class CustomDocumentAdapter(
	context: Context,
	private val documents: List<JumioDocument>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

	init {
		documents.forEach {
			if (it is JumioPhysicalDocument) {
				add("${it.type.name} - ${it.variant.name}")
			} else if (it is JumioDigitalDocument) {
				add(it.type)
			}
		}
	}

	fun getDocument(position: Int): JumioDocument {
		return documents.elementAt(position)
	}
}

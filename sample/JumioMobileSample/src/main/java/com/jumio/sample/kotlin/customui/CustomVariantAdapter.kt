// Copyright 2022 Jumio Corporation, all rights reserved.
package com.jumio.sample.kotlin.customui

import android.content.Context
import android.widget.ArrayAdapter
import com.jumio.sdk.document.JumioDocumentVariant

/**
 * Array adapter implementation for the variant spinner
 */
class CustomVariantAdapter(context: Context, documentVariantSet: List<JumioDocumentVariant>) :
	ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

	private val documentVariants: Array<JumioDocumentVariant> = documentVariantSet.toTypedArray()

	init {
		documentVariants.map { it.name }.also { addAll(it) }
	}

	/**
	 * Return the [JumioDocumentVariant] for the current position
	 *
	 * @param position
	 * @return [JumioDocumentVariant]
	 */
	fun getDocumentVariant(position: Int) = documentVariants[position]
}

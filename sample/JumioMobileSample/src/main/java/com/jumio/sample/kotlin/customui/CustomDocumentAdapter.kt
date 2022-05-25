// Copyright 2022 Jumio Corporation, all rights reserved.
package com.jumio.sample.kotlin.customui

import android.content.Context
import android.widget.ArrayAdapter
import com.jumio.sdk.document.JumioDocument
import com.jumio.sdk.document.JumioDocumentType
import com.jumio.sdk.document.JumioDocumentVariant

/**
 * Array adapter implementation for the document spinner
 */
class CustomDocumentAdapter(context: Context, documentList: List<JumioDocument>) :
	ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

	private val documentTypeList: Set<JumioDocumentType>
	private val documentVariantMap: LinkedHashMap<JumioDocumentType, ArrayList<JumioDocumentVariant>>

	init {
		val map = documentList.groupBy { it.type }
		documentTypeList = map.keys
		documentVariantMap = LinkedHashMap()
		documentTypeList.forEach { documentType ->
			add(documentType.name)
			val variantList = ArrayList<JumioDocumentVariant>()
			map[documentType]?.forEach { jumioDocument ->
				variantList.add(jumioDocument.variant)
			}
			documentVariantMap[documentType] = variantList
		}

	}

	/**
	 * Return the [JumioDocumentType] for the current position
	 *
	 * @param position
	 * @return [JumioDocumentType]
	 */
	fun getDocumentType(position: Int) = documentTypeList.elementAt(position)

	/**
	 * Gets all the available [JumioDocumentVariant] for the specified {@link JumioDocumentType}
	 *
	 * @param documentType
	 * @return List of [JumioDocumentVariant]
	 */
	fun getDocumentVariants(documentType: JumioDocumentType) = documentVariantMap[documentType] ?: emptyList()
}
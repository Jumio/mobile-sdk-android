// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.extension

import com.jumio.sdk.document.JumioDigitalDocument
import com.jumio.sdk.document.JumioDocument
import com.jumio.sdk.document.JumioPhysicalDocument

fun JumioDocument.getName() = when (this) {
	is JumioPhysicalDocument -> {
		"${this.type.name} - ${this.variant.name}"
	}
	is JumioDigitalDocument -> {
		this.type
	}
	else -> {
		""
	}
}

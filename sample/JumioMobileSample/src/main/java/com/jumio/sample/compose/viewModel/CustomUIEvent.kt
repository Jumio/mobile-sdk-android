// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.viewModel

import com.jumio.sdk.document.JumioDocument
import com.jumio.sdk.enums.JumioAcquireMode

sealed class CustomUIEvent {
	data object StartClicked : CustomUIEvent()
	data class CountrySelected(val country: String) : CustomUIEvent()
	data class DocumentSelected(val document: JumioDocument) : CustomUIEvent()
	data class AcquireModeClicked(val mode: JumioAcquireMode) : CustomUIEvent()
}

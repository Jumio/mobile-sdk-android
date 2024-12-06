// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.viewModel

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.jumio.sdk.enums.JumioDataCenter

class ViewModelFactory(
	owner: SavedStateRegistryOwner,
	private val application: Application,
	private val token: String,
	private val dataCenter: JumioDataCenter,
	private val customTheme: Int,
) : AbstractSavedStateViewModelFactory(owner, null) {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
		return CustomUIViewModel(
			savedStateHandle = handle,
			application = application,
			token = token,
			dataCenter = dataCenter,
			customTheme = customTheme
		) as T
	}
}

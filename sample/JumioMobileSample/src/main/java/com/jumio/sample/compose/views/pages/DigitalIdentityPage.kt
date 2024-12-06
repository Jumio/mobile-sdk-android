// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sdk.views.JumioDigitalIdentityView

/**
 * shows the DigitalIdentityView to scan the digital identities
 */
@Composable
fun DigitalIdentityPage(viewModel: CustomUIViewModel, modifier: Modifier = Modifier) {
	Box(modifier = modifier.fillMaxSize()) {
		AndroidView(
			factory = {
				val digitalIdentityView = JumioDigitalIdentityView(it)
				viewModel.currentScanPart?.let { scanPart ->
					digitalIdentityView.attach(scanPart)
				}
				digitalIdentityView
			},
			modifier = Modifier.fillMaxSize()
		)
	}
}

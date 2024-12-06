// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.views.JumioConfirmationView

/**
 * Shows the list of scanned parts with options of retake or confirm the scan
 */
@Composable
fun ConfirmationPage(viewModel: CustomUIViewModel, modifier: Modifier = Modifier, onClose: () -> Unit) {
	BackHandler {
		viewModel.confirmationHandler.retake()
	}
	Column(modifier = modifier.padding(vertical = MaterialTheme.spacing.medium).fillMaxSize()) {
		IconButton(onClick = {
			onClose()
		}) {
			Icon(
				imageVector = Icons.Default.Clear,
				contentDescription = stringResource(id = R.string.close),
				tint = MaterialTheme.colors.primary
			)
		}
		viewModel.currentScanPart?.let { jumioScanPart ->
			viewModel.confirmationHandler.attach(jumioScanPart)
			LazyColumn(modifier = Modifier.padding(all = MaterialTheme.spacing.medium).weight(0.9f)) {
				items(viewModel.confirmationHandler.parts) {
					val confirmationView = JumioConfirmationView(LocalContext.current)
					viewModel.confirmationHandler.renderPart(it, confirmationView)
					AndroidView(factory = {
						confirmationView
					})
				}
			}
		}
		Row(
			horizontalArrangement = Arrangement.SpaceEvenly,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth().weight(0.1f)
		) {
			PrimaryButton(
				title = stringResource(id = R.string.retake),
				onClick = {
					viewModel.confirmationHandler.retake()
				}
			)
			Spacer(modifier = Modifier.width(MaterialTheme.spacing.large))
			PrimaryButton(
				title = stringResource(id = R.string.confirm),
				onClick = {
					viewModel.confirmationHandler.confirm()
				}
			)
		}
	}
}

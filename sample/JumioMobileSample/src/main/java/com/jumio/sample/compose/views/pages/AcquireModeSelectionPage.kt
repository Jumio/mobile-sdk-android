// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.viewModel.CustomUIEvent
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sdk.credentials.JumioDocumentCredential
import com.jumio.sdk.enums.JumioAcquireMode

/**
 * Shows the acquire mode [JumioAcquireMode] options for a [JumioDocumentCredential]
 * [JumioAcquireMode.FILE] to select the pdf file
 * [JumioAcquireMode.CAMERA] to take a photo
 */
@Composable
fun AcquireModeSelectionPage(viewModel: CustomUIViewModel, modifier: Modifier = Modifier, onClose: () -> Unit) {
	Box {
		Column(
			modifier = modifier
				.padding(vertical = MaterialTheme.spacing.medium)
		) {
			IconButton(onClick = {
				onClose()
			}) {
				Icon(
					imageVector = Icons.Default.Clear,
					contentDescription = stringResource(id = R.string.close),
					tint = MaterialTheme.colors.primary
				)
			}
			Text(
				text = stringResource(id = R.string.select_acquire_mode),
				color = MaterialTheme.colors.label,
				modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
			)
			(viewModel.currentCredential as? JumioDocumentCredential)?.let {
				LazyColumn(modifier = Modifier.padding(start = MaterialTheme.spacing.large)) {
					items(it.availableAcquireModes) {
						Column {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier
									.padding(
										top = MaterialTheme.spacing.small,
										bottom = MaterialTheme.spacing.small,
										end = MaterialTheme.spacing.medium
									)
									.clickable {
										viewModel.onUiEvent(CustomUIEvent.AcquireModeClicked(it))
									}
							) {
								Text(
									text = it.name,
									color = MaterialTheme.colors.label,
									modifier = Modifier.weight(1f)
								)
								Icon(
									imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
									contentDescription = stringResource(id = R.string.camera),
									tint = MaterialTheme.colors.primary
								)
							}
							HorizontalDivider()
						}
					}
				}
			}
		}
	}
}

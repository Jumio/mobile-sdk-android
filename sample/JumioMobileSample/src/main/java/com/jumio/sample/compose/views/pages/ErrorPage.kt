// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton

/**
 * Shows error message with retry option on [JumioError] or [JumioScanStep.RETRY]
 */
@Composable
fun ErrorPage(
	message: String,
	isRetryable: Boolean,
	onRetryClick: () -> Unit,
	onClose: () -> Unit,
	modifier: Modifier = Modifier,
) {
	BackHandler {
		if (isRetryable) {
			onRetryClick()
		} else {
			onClose()
		}
	}
	Column(modifier = modifier.padding(vertical = MaterialTheme.spacing.medium)) {
		IconButton(onClick = {
			onClose()
		}) {
			Icon(
				imageVector = Icons.Filled.Close,
				contentDescription = stringResource(id = R.string.close),
				tint = MaterialTheme.colors.primary
			)
		}
		Spacer(modifier = modifier.height(MaterialTheme.spacing.extraLarge))
		Column(
			modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.medium),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = message,
				color = MaterialTheme.colors.error,
				textAlign = TextAlign.Center
			)
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
			PrimaryButton(
				title = stringResource(id = if (isRetryable) R.string.retry else R.string.cancel).uppercase()
			) {
				if (isRetryable) {
					onRetryClick()
				} else {
					onClose()
				}
			}
		}
	}
}

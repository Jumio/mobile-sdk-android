// Copyright 2026 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.consent.JumioFasterVerification

@Composable
fun FasterVerificationPage(
	modifier: Modifier = Modifier,
	fasterVerification: JumioFasterVerification,
	onContinue: () -> Unit,
	onScanManually: () -> Unit,
	onBackPress: () -> Unit,
) {
	BackHandler {
		onBackPress()
	}
	Column(modifier = modifier.fillMaxSize()) {
		IconButton(onClick = { onBackPress() }) {
			Icon(
				painter = painterResource(R.drawable.ic_keyboard_arrow_left),
				contentDescription = stringResource(id = R.string.back),
				tint = MaterialTheme.colors.primary,
				modifier = Modifier.size(MaterialTheme.spacing.superLarge)
			)
		}
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.compact))

		Text(
			text = stringResource(id = R.string.jumio_selfiedone_faster_verification_title),
			style = MaterialTheme.typography.headlineMedium,
			textAlign = TextAlign.Center,
			maxLines = 1,
			modifier = Modifier.fillMaxWidth().padding(all = MaterialTheme.spacing.medium)
		)

		Column(
			modifier = Modifier
				.weight(1f)
				.padding(all = MaterialTheme.spacing.medium),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.semiLarge))

			Text(
				text = fasterVerification.legalStatement.text,
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(MaterialTheme.spacing.small)
			)

			PrimaryButton(
				title = stringResource(id = R.string.jumio_selfiedone_yes_continue_button),
				onClick = onContinue,
				modifier = Modifier.fillMaxWidth()
			)

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

			PrimaryButton(
				title = stringResource(id = R.string.jumio_selfiedone_no_scan_id_manually_button),
				onClick = onScanManually,
				modifier = Modifier.fillMaxWidth()
			)
		}
	}
}

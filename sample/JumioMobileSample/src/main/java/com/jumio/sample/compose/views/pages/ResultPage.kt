// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.jumio.core.result.JumioDocumentResult
import com.jumio.sample.R
import com.jumio.sample.compose.theme.Typography
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.result.JumioCredentialResult
import com.jumio.sdk.result.JumioFaceResult
import com.jumio.sdk.result.JumioIDResult
import com.jumio.sdk.result.JumioResult

/**
 * Shows the result of all scans such as ID, Face or Document.
 */
@Composable
fun ResultPage(jumioResult: JumioResult, modifier: Modifier = Modifier, onDismiss: () -> Unit) {
	Column(
		modifier = modifier
			.padding(horizontal = MaterialTheme.spacing.medium)
			.padding(bottom = MaterialTheme.spacing.medium)
			.verticalScroll(rememberScrollState())
	) {
		Text(
			text = stringResource(id = R.string.scan_finish_msg),
			style = Typography.headlineMedium,
			color = MaterialTheme.colors.label,
			modifier = Modifier.fillMaxWidth(),
			textAlign = TextAlign.Center
		)
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
		if (jumioResult.isSuccess) {
			jumioResult.credentialInfos?.forEach {
				when (val result = jumioResult.getResult(it)) {
					is JumioIDResult -> {
						TextPrimaryLabel(label = stringResource(id = R.string.id))
						Column(modifier = Modifier.padding(start = MaterialTheme.spacing.medium)) {
							val firstName = result.firstName
							val lastName = result.lastName
							if (firstName != null && lastName != null) {
								TextSecondaryLabel(label = "Name: ${firstName.uppercase()} ${lastName.uppercase()}")
							}
							result.country?.let { country ->
								TextSecondaryLabel(label = "Country: ${country.uppercase()}")
							}
						}
					}
					is JumioFaceResult -> {
						TextPrimaryLabel(label = stringResource(id = R.string.face))
						val faceResult = when (result.passed) {
							true -> "Yes"
							false -> "No"
							else -> "N/A"
						}
						TextSecondaryLabel(label = "Passed: $faceResult")
					}
					is JumioDocumentResult -> {
						result.extractionMode?.name?.let { name ->
							TextPrimaryLabel(label = stringResource(id = R.string.document))
							TextPrimaryLabel(label = "Mode: $name")
						}
					}
					is JumioCredentialResult -> {
						result.extractionMode?.name?.let { name ->
							TextPrimaryLabel(label = stringResource(id = R.string.credentials))
							TextPrimaryLabel(label = "Mode: $name")
						}
					}
				}
			}
		} else {
			jumioResult.error?.let { error ->
				TextPrimaryLabel(label = stringResource(id = R.string.error))
				Column(modifier = Modifier.padding(start = MaterialTheme.spacing.medium)) {
					TextSecondaryLabel(label = "Code: ${error.code}")
					TextSecondaryLabel(label = "Message: ${error.message}")
				}
			}
		}
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
		PrimaryButton(
			title = stringResource(id = R.string.done).uppercase(),
			modifier = Modifier.fillMaxWidth()
		) {
			onDismiss()
		}
	}
}

@Composable
fun TextSecondaryLabel(label: String) {
	Text(
		text = label,
		color = MaterialTheme.colors.label,
		modifier = Modifier.padding(all = MaterialTheme.spacing.medium)
	)
	HorizontalDivider()
}

@Composable
fun TextPrimaryLabel(label: String) {
	Text(
		text = label,
		style = Typography.labelLarge,
		color = MaterialTheme.colors.label,
		modifier = Modifier.padding(all = MaterialTheme.spacing.medium)
	)
	HorizontalDivider()
}

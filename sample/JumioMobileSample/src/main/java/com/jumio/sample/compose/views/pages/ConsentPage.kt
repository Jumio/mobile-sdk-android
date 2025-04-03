// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumio.sample.R
import com.jumio.sample.compose.theme.Typography
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.viewModel.CustomUIEvent
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.enums.JumioConsentType

/**
 * Consent Page to show the list of credentials to scan and the list of privacy policy to accept
 * and on click of start button would trigger the start of the workflow for first credential
 */
@Composable
fun ConsentPage(viewModel: CustomUIViewModel, modifier: Modifier = Modifier, onClose: () -> Unit) {
	val loaderState by viewModel.consentPageLoaderState.collectAsStateWithLifecycle()
	val credentialInfoList by viewModel.credentialInfoList.collectAsState()
	val consentItems by viewModel.consentItems.collectAsState()
	Column(modifier = modifier.fillMaxSize()) {
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
		Text(
			text = stringResource(id = R.string.workflow_consists),
			style = Typography.titleLarge,
			color = MaterialTheme.colors.label,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = MaterialTheme.spacing.large)
		)
		Spacer(modifier = modifier.height(MaterialTheme.spacing.medium))
		if (loaderState) {
			Text(
				text = stringResource(id = R.string.loading),
				style = Typography.titleMedium,
				color = MaterialTheme.colors.label,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = MaterialTheme.spacing.superLarge)
			)
		}

		LazyColumn {
			items(credentialInfoList) { info ->
				Text(
					text = "• capture your " + info.category.name,
					style = Typography.titleMedium,
					color = MaterialTheme.colors.label,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = MaterialTheme.spacing.superLarge)
				)
			}
		}

		Spacer(modifier = Modifier.weight(1f))

		LazyColumn {
			items(consentItems) { consentItem ->
				val checked = remember { mutableStateOf(false) }
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = MaterialTheme.spacing.medium)
				) {
					if (consentItem.type == JumioConsentType.ACTIVE) {
						Checkbox(
							checked = checked.value,
							onCheckedChange = {
								checked.value = it
								viewModel.onConsentItemToggle(consentItem, it)
							},
							colors = CheckboxDefaults.colors().copy(
								checkedCheckmarkColor = MaterialTheme.colors.primary,
								checkedBoxColor = MaterialTheme.colors.checkBox,
								checkedBorderColor = MaterialTheme.colors.primary
							)
						)
					}

					val textColor = MaterialTheme.colors.label
					AndroidView(factory = { context ->
						TextView(context).apply {
							text = consentItem.spannedTextWithLinkColor(Color.BLUE)
							setTextColor(textColor.toArgb())
							textSize = 16f
							movementMethod = LinkMovementMethod.getInstance()
						}
					})
				}
			}
		}

		Spacer(modifier = modifier.height(MaterialTheme.spacing.superLarge))

		val titleId = if (loaderState) {
			R.string.loading
		} else {
			R.string.start
		}
		PrimaryButton(
			title = stringResource(id = titleId),
			modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.medium),
			enabled = !loaderState
		) {
			viewModel.onUiEvent(CustomUIEvent.StartClicked)
		}
		Spacer(modifier = modifier.height(MaterialTheme.spacing.medium))
	}
}

// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jumio.sample.R
import com.jumio.sample.compose.theme.Typography
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.dimensions
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.enums.JumioScanStep
import com.jumio.sdk.enums.JumioScanUpdate
import com.jumio.sdk.views.JumioAnimationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun NfcScanPage(
	viewModel: CustomUIViewModel,
	onClose: () -> Unit,
	onBackPress: () -> Unit,
	modifier: Modifier = Modifier,
) {
	BackHandler {
		onBackPress()
	}
	val context = LocalContext.current
	val title = remember { mutableStateOf(context.getString(com.jumio.defaultui.R.string.jumio_nfc_header_start)) }
	val description =
		remember { mutableStateOf(context.getString(com.jumio.defaultui.R.string.jumio_nfc_description_start_other)) }
	val progress = remember { mutableIntStateOf(0) }
	val showProgress = remember { mutableStateOf(false) }
	val showIvStatus = remember { mutableStateOf(false) }
	val showSkipButton = remember { mutableStateOf(true) }
	val lifecycleOwner = LocalLifecycleOwner.current
	val scope = rememberCoroutineScope()
	val animatedProgress by animateFloatAsState(
		targetValue = progress.intValue / 100f,
		animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
		label = ""
	)

	DisposableEffect(lifecycleOwner) {
		val scanUpdateJob = scope.launch {
			viewModel.scanUpdateEvent.collectLatest {
				it?.let { (scanUpdate, data) ->
					when (scanUpdate) {
						JumioScanUpdate.NFC_EXTRACTION_STARTED -> {
							showIvStatus.value = false
							showSkipButton.value = false
							showProgress.value = true
							title.value = context.getString(com.jumio.defaultui.R.string.jumio_nfc_header_extracting)
							description.value = ""
							progress.intValue = 0
						}
						JumioScanUpdate.NFC_EXTRACTION_PROGRESS -> {
							showProgress.value = true
							showIvStatus.value = false
							progress.intValue = data as? Int ?: 0
						}
						JumioScanUpdate.NFC_EXTRACTION_FINISHED -> {
							showIvStatus.value = true
							showProgress.value = false
						}
						else -> {
						}
					}
				}
			}
		}

		val scanStepJob = scope.launch {
			viewModel.scanStepEvent.collectLatest { scanStep ->
				when (scanStep) {
					JumioScanStep.ADDON_SCAN_PART -> {
						showIvStatus.value = false
						showSkipButton.value = true
						showProgress.value = false
						title.value = context.getString(com.jumio.defaultui.R.string.jumio_nfc_header_start)
						description.value = context.getString(com.jumio.defaultui.R.string.jumio_nfc_description_start_other)
					}
					else -> {}
				}
			}
		}

		onDispose {
			scanUpdateJob.cancel()
			scanStepJob.cancel()
		}
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = MaterialTheme.spacing.medium)
			.padding(vertical = MaterialTheme.spacing.medium)
	) {
		IconButton(onClick = {
			onClose()
		}) {
			Icon(
				imageVector = Icons.Filled.Close,
				contentDescription = stringResource(id = R.string.close),
				tint = MaterialTheme.colors.primary
			)
		}
		Spacer(modifier = modifier.height(MaterialTheme.spacing.large))
		Column(
			modifier = Modifier.fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = title.value,
				style = Typography.headlineLarge,
				color = MaterialTheme.colors.label,
				modifier = Modifier.fillMaxWidth(),
				textAlign = TextAlign.Center
			)
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
			AndroidView(
				factory = {
					JumioAnimationView(it).apply {
						viewModel.currentScanPart?.getHelpAnimation(this)
					}
				},
				modifier = Modifier.height(MaterialTheme.dimensions.animationViewSize).fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
			Text(
				text = description.value,
				style = Typography.titleMedium,
				color = MaterialTheme.colors.label,
				modifier = Modifier.fillMaxWidth(),
				textAlign = TextAlign.Center
			)

			if (showProgress.value) {
				Box(
					modifier = Modifier.fillMaxWidth(),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator(
						progress = { animatedProgress },
						modifier = Modifier.width(MaterialTheme.spacing.superLarge)
							.height(MaterialTheme.spacing.superLarge),
						color = MaterialTheme.colorScheme.secondary,
						trackColor = MaterialTheme.colors.primary
					)
					Text(
						text = "${progress.intValue}%",
						color = MaterialTheme.colors.label,
						modifier = Modifier.padding(MaterialTheme.spacing.medium)
					)
				}
				Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
				Text(
					text = stringResource(id = com.jumio.defaultui.R.string.jumio_nfc_description_extracting),
					style = Typography.titleMedium,
					color = MaterialTheme.colors.label,
					modifier = Modifier.fillMaxWidth(),
					textAlign = TextAlign.Center
				)
			}

			if (showIvStatus.value) {
				Icon(
					imageVector = Icons.Filled.CheckCircle,
					contentDescription = stringResource(id = R.string.nfc_scan_finished),
					tint = MaterialTheme.colors.primary
				)
			}
		}

		Spacer(modifier = Modifier.weight(1f))
		if (showSkipButton.value) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center
			) {
				PrimaryButton(
					title = stringResource(id = com.jumio.defaultui.R.string.jumio_nfc_button_skip),
					onClick = {
						viewModel.skipAddonPart()
					}
				)
			}
		}
	}
}

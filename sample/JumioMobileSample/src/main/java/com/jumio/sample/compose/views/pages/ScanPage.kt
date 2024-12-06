// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.enums.JumioScanStep
import com.jumio.sdk.enums.JumioScanUpdate
import com.jumio.sdk.views.JumioScanView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A scanPage to show the JumioScanview to display the camera with various controls such as extraction method, fallback, flash and
 * manual capture
 */

@Composable
fun ScanPage(viewModel: CustomUIViewModel, modifier: Modifier = Modifier) {
	val scanAlignment = viewModel.scanAlignmentState.collectAsStateWithLifecycle()
	val flipDocument = viewModel.flipDocument.collectAsStateWithLifecycle()
	val lifecycleOwner = LocalLifecycleOwner.current
	val scope = rememberCoroutineScope()
	val scanView = remember { mutableStateOf<JumioScanView?>(null) }
	val takePicture = remember { mutableStateOf(false) }
	val switchCamera = remember { mutableStateOf(false) }
	val toggleFlash = remember { mutableStateOf(false) }
	val startFallback = remember { mutableStateOf(false) }

	DisposableEffect(lifecycleOwner) {
		val scanStepJob = scope.launch {
			viewModel.scanStepEvent.collectLatest { scanStep ->
				when (scanStep) {
					JumioScanStep.SCAN_VIEW -> {
						toggleFlash.value = false
						switchCamera.value = false
						takePicture.value = false
						startFallback.value = viewModel.currentScanPart?.hasFallback == true
					}
					JumioScanStep.CAN_FINISH -> {
						scanView.value?.let {
							lifecycleOwner.lifecycle.removeObserver(it)
						}
					}
					JumioScanStep.NEXT_PART -> {
						startFallback.value = viewModel.currentScanPart?.hasFallback == true
						scanView.value?.let {
							takePicture.value = it.isShutterEnabled
							it.extraction = false
							delay(3000)
							it.extraction = true
						}
					}
					else -> {}
				}
			}
		}

		val scanUpdateJob = scope.launch {
			viewModel.scanUpdateEvent.collectLatest { scanUpdate ->
				scanUpdate?.let { (jumioScanUpdate, _) ->
					when (jumioScanUpdate) {
						JumioScanUpdate.CAMERA_AVAILABLE -> {
							viewModel.scanUpdateEvent.value = null
							scanView.value?.let {
								toggleFlash.value = it.hasFlash
								switchCamera.value = it.hasMultipleCameras
								takePicture.value = it.isShutterEnabled
							}
						}
						JumioScanUpdate.FALLBACK -> {
							viewModel.scanUpdateEvent.value = null
							startFallback.value = viewModel.currentScanPart?.hasFallback == true
							scanView.value?.let {
								takePicture.value = it.isShutterEnabled
							}
						}
						else -> {
						}
					}
				}
			}
		}

		onDispose {
			scanView.value?.let { jumioScanView ->
				lifecycleOwner.lifecycle.removeObserver(jumioScanView)
			}
			scanStepJob.cancel()
			scanUpdateJob.cancel()
		}
	}

	Box(modifier = modifier.fillMaxSize()) {
		AndroidView(
			factory = {
				JumioScanView(it).apply {
					invalidate()
					requestLayout()
					viewModel.currentScanPart?.let { scanPart ->
						attach(scanPart)
					}
					lifecycleOwner.lifecycle.addObserver(this)
				}.also { view ->
					scanView.value = view
				}
			},
			modifier = Modifier.fillMaxSize()
		)
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.superLarge)
		) {
			Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
				viewModel.currentScanPart?.scanMode?.let {
					PrimaryButton(
						title = it.name,
						onClick = {}
					)
				}
				Spacer(modifier = Modifier.width(MaterialTheme.spacing.large))
				PrimaryButton(
					title = stringResource(id = R.string.fallback),
					enabled = startFallback.value,
					onClick = {
						viewModel.currentScanPart?.fallback()
					}
				)
			}

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

			Text(
				text = flipDocument.value,
				color = MaterialTheme.colors.primary,
				style = MaterialTheme.typography.labelMedium
			)

			Text(
				text = scanAlignment.value,
				color = MaterialTheme.colors.label
			)

			Spacer(modifier = Modifier.weight(1f))

			PrimaryButton(
				title = stringResource(id = R.string.take),
				enabled = takePicture.value,
				modifier = Modifier.visibility(takePicture.value),
				onClick = {
					scanView.value?.takePicture()
				}
			)
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
			Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
				PrimaryButton(
					title = stringResource(id = R.string.camera),
					enabled = switchCamera.value,
					modifier = Modifier.visibility(switchCamera.value),
					onClick = {
						scanView.value?.switchCamera()
					}
				)
				Spacer(modifier = Modifier.width(MaterialTheme.spacing.large))
				PrimaryButton(
					title = stringResource(id = R.string.flash),
					enabled = toggleFlash.value,
					modifier = Modifier.visibility(toggleFlash.value),
					onClick = {
						scanView.value?.let {
							it.flash = !it.flash
						}
					}
				)
			}
		}
	}
}

private fun Modifier.visibility(visible: Boolean): Modifier {
	return layout { measurable, constraints ->
		val placeable = measurable.measure(constraints)

		layout(placeable.width, placeable.height) {
			if (visible) {
				// place this item in the original position
				placeable.placeRelative(0, 0)
			}
		}
	}
}

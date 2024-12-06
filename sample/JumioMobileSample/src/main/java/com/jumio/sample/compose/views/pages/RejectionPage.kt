// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import android.widget.LinearLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.jumio.commons.utils.dpToPx
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.views.JumioRejectView

/**
 * Shows the list of rejected scanned parts and option to retake the scan
 */
@Composable
fun RejectionPage(viewModel: CustomUIViewModel, modifier: Modifier = Modifier, onClose: () -> Unit) {
	BackHandler {
		viewModel.rejectHandler.retake()
	}
	Box {
		Column(
			modifier = modifier
				.padding(all = MaterialTheme.spacing.medium)
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
			LazyColumn(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
				horizontalAlignment = Alignment.CenterHorizontally,
				contentPadding = PaddingValues(
					horizontal = MaterialTheme.spacing.medium,
					vertical = MaterialTheme.spacing.small
				)
			) {
				items(viewModel.rejectHandler.parts) { part ->
					AndroidView(factory = { context ->
						val rejectionView = JumioRejectView(context).apply {
							cornerRadius = 16.dpToPx(context)
							layoutParams = LinearLayout.LayoutParams(
								300.dpToPx(context),
								300.dpToPx(context)
							)
						}
						viewModel.rejectHandler.renderPart(part, rejectionView)
						rejectionView
					})
				}
			}
		}
		Column {
			Spacer(modifier = Modifier.weight(1f))
			Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
				PrimaryButton(
					title = stringResource(id = R.string.reject),
					onClick = {
						viewModel.rejectHandler.retake()
					}
				)
			}
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
		}
	}
}

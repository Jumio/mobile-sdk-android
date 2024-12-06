// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import com.jumio.sample.compose.theme.Typography
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.views.JumioFileAttacher

/**
 * Help Page to show the requirement details to upload the file
 */
@Composable
fun UploadFileHelpPage(
	requirements: JumioFileAttacher.JumioFileRequirements,
	onSelectFile: () -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.padding(vertical = MaterialTheme.spacing.medium)) {
		IconButton(onClick = {
			onBack()
		}) {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = stringResource(id = R.string.back),
				tint = MaterialTheme.colors.primary
			)
		}
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxWidth().weight(1f)
		) {
			Text(
				text = stringResource(R.string.requirement),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center,
				style = Typography.titleLarge
			)
			Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
			Text(
				text = stringResource(
					com.jumio.defaultui.R.string.jumio_dv_upload_tips_file_size,
					"%dMB".format(
						requirements.maxFileSize / (1024 * 1024)
					)
				),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
			Text(
				text = stringResource(com.jumio.defaultui.R.string.jumio_dv_upload_tips_page_size, requirements.pdfMaxPages),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
			Text(
				text = stringResource(com.jumio.defaultui.R.string.jumio_dv_upload_tips_protected),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
		}
		Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
			PrimaryButton(
				title = stringResource(id = R.string.select_file),
				onClick = {
					onSelectFile()
				}
			)
		}
	}
}

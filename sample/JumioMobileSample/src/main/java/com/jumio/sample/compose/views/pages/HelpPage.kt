// (c) 2026 Jumio All rights reserved. US Patent App.
package com.jumio.sample.compose.views.pages

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.jumio.sample.R
import com.jumio.sample.compose.theme.Typography
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sample.compose.views.molecules.FileUploadBottomSheet
import com.jumio.sample.xml.MIME_TYPE_ALL
import com.jumio.sample.xml.MIME_TYPE_IMAGE
import com.jumio.sample.xml.MIME_TYPE_PDF
import com.jumio.sdk.views.JumioFileAttacher
import kotlinx.coroutines.launch

/**
 * Help Page to show the requirement details to upload the file
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadFileHelpPage(
	requirements: JumioFileAttacher.JumioFileRequirements,
	onSelectFile: (Intent) -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val scope = rememberCoroutineScope()
	var showBottomSheet by remember { mutableStateOf(false) }

	if (showBottomSheet) {
		FileUploadBottomSheet(
			sheetState = sheetState,
			onDismiss = { showBottomSheet = false },
			onFileSystemClick = {
				scope.launch { sheetState.hide() }.invokeOnCompletion {
					if (!sheetState.isVisible) {
						showBottomSheet = false
						val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
							addCategory(Intent.CATEGORY_OPENABLE)
							putExtra(Intent.EXTRA_MIME_TYPES, requirements.mimeTypes.toTypedArray())
							type = MIME_TYPE_ALL
						}
						onSelectFile(intent)
					}
				}
			},
			onPhotoLibraryClick = {
				scope.launch { sheetState.hide() }.invokeOnCompletion {
					if (!sheetState.isVisible) {
						showBottomSheet = false
						val intent = Intent(Intent.ACTION_PICK).apply {
							putExtra(Intent.EXTRA_MIME_TYPES, requirements.mimeTypes.filter { type -> type != MIME_TYPE_PDF }.toTypedArray())
							type = MIME_TYPE_IMAGE
						}
						onSelectFile(intent)
					}
				}
			}
		)
	}
	Column(modifier = modifier.padding(vertical = MaterialTheme.spacing.medium)) {
		IconButton(onClick = {
			onBack()
		}) {
			Icon(
				painter = painterResource(R.drawable.ic_keyboard_arrow_left),
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
				text = stringResource(R.string.pdf_file),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center,
				style = Typography.titleMedium
			)
			Text(
				text = stringResource(
					R.string.jumio_dv_upload_tips_file_size,
					"%dMB".format(
						requirements.maxFileSize / (1024 * 1024)
					)
				),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
			Text(
				text = stringResource(R.string.jumio_dv_upload_tips_page_size, requirements.pdfMaxPages),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
			Text(
				text = stringResource(R.string.jumio_dv_upload_tips_protected),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
			Text(
				text = stringResource(R.string.image_file),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center,
				style = Typography.titleMedium
			)
			Text(
				text = stringResource(R.string.jumio_dv_jpg_png_or_webp_format, requirements.pdfMaxPages),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
			Text(
				text = stringResource(R.string.jumio_dv_clear_or_unedited),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
			Text(
				text = stringResource(
					R.string.jumio_dv_upload_tips_file_size,
					"%dMB".format(
						requirements.maxFileSize / (1024 * 1024)
					)
				),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
			Text(
				text = stringResource(
					R.string.jumio_dv_no_transparency_or_watermarks,
					requirements.pdfMaxPages
				),
				color = MaterialTheme.colors.label,
				textAlign = TextAlign.Center
			)
		}
		Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
			PrimaryButton(
				title = stringResource(id = R.string.select_file),
				onClick = {
					showBottomSheet = true
				}
			)
		}
	}
}

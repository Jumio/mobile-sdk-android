package com.jumio.sample.compose.views.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumio.sample.R
import com.jumio.sample.compose.views.atoms.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileUploadBottomSheet(
	sheetState: SheetState,
	onDismiss: () -> Unit,
	onFileSystemClick: () -> Unit,
	onPhotoLibraryClick: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			PrimaryButton(
				onClick = onFileSystemClick,
				modifier = Modifier.fillMaxWidth(),
				title = stringResource(R.string.file_source_file_system)
			)
			PrimaryButton(
				onClick = onPhotoLibraryClick,
				modifier = Modifier.fillMaxWidth(),
				title = stringResource(R.string.file_source_photo_library)
			)
		}
	}
}

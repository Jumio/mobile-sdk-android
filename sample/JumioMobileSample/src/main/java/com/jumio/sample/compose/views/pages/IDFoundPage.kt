// Copyright 2026 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import android.content.Context
import android.widget.LinearLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jumio.core.views.BrandingView
import com.jumio.core.views.SelfieDoneBrandingView
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.dimensions
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sdk.document.JumioDocumentType
import com.jumio.sdk.document.JumioLookupResult

@Composable
fun IDFoundPage(
	modifier: Modifier = Modifier,
	lookupResult: JumioLookupResult,
	onContinue: () -> Unit,
	onScanManually: () -> Unit,
	onBackPress: () -> Unit,
) {
	BackHandler {
		onBackPress()
	}
	Column(modifier = modifier.fillMaxSize().padding(all = MaterialTheme.spacing.medium)) {
		IconButton(onClick = { onBackPress() }) {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = stringResource(id = R.string.back),
				tint = MaterialTheme.colors.primary
			)
		}
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.compact))
		Column(
			modifier = Modifier
				.weight(1f)
				.padding(bottom = MaterialTheme.spacing.medium),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = stringResource(id = R.string.jumio_selfiedone_ID_Found),
				style = MaterialTheme.typography.headlineMedium,
				textAlign = TextAlign.Center,
				maxLines = 1,
				modifier = Modifier.fillMaxWidth()
			)

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

			Text(
				text = stringResource(id = R.string.jumio_selfiedone_we_found_your_ID),
				style = MaterialTheme.typography.bodyLarge,
				textAlign = TextAlign.Center,
				modifier = Modifier.fillMaxWidth()
			)

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

			Card(
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(MaterialTheme.dimensions.selfieDoneIdRadius),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colors.jumioSelfieDoneIdBackground
				)
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = MaterialTheme.spacing.semiLarge),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Icon(
						imageVector = Icons.Default.Person,
						contentDescription = null,
						modifier = Modifier.size(MaterialTheme.dimensions.selfieDoneIdHeight)
					)

					Text(
						text = lookupResult.documentType.getLocalizedName(LocalContext.current),
						style = MaterialTheme.typography.titleLarge,
						textAlign = TextAlign.Center,
						maxLines = 1
					)
				}
			}

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.semiLarge))

			Divider(
				modifier = Modifier.fillMaxWidth().height(1.dp),
				color = Color(0xFFCCCCCC)
			)

			Text(
				text = lookupResult.legalStatement.text,
				style = MaterialTheme.typography.bodySmall,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(MaterialTheme.spacing.small)
			)

			PrimaryButton(
				title = stringResource(id = R.string.jumio_selfiedone_continue),
				onClick = onContinue,
				modifier = Modifier.fillMaxWidth()
			)

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

			PrimaryButton(
				title = stringResource(id = R.string.jumio_selfiedone_scan_ID_manually),
				onClick = onScanManually,
				modifier = Modifier.fillMaxWidth()
			)
		}

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = MaterialTheme.spacing.medium),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			AndroidView(
				factory = { context: Context -> generateSelfieDoneLogo(context) },
				modifier = Modifier.wrapContentSize()
			)

			Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

			AndroidView(
				factory = { context: Context -> generatePoweredByLogo(context) },
				modifier = Modifier.wrapContentSize()
			)
		}
	}
}

private fun generatePoweredByLogo(context: Context): BrandingView =
	BrandingView(context, R.color.jumio_selfie_done_branding).apply {
		this.layoutParams = LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		)
	}

private fun generateSelfieDoneLogo(context: Context): SelfieDoneBrandingView =
	SelfieDoneBrandingView(context, R.color.jumio_selfie_done_branding).apply {
		this.layoutParams = LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		)
	}

@Preview(showBackground = true)
@Composable
fun IDFoundPagePreview() {
	MaterialTheme {
		IDFoundPage(
			lookupResult = JumioLookupResult(JumioDocumentType.DRIVING_LICENSE, JumioLookupResult.JumioLegalStatement("....")),
			onBackPress = {},
			onContinue = {},
			onScanManually = {}
		)
	}
}

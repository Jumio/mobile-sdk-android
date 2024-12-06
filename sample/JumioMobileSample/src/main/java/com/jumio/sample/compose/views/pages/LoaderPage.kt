// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing

/**
 * Loader page to show during different scan steps such as verifying, processing and preparing
 */
@Composable
fun LoaderPage(title: String, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		CircularProgressIndicator(
			modifier = Modifier.width(48.dp),
			color = MaterialTheme.colorScheme.secondary,
			trackColor = MaterialTheme.colors.primary
		)
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
		Text(
			text = title,
			color = MaterialTheme.colors.label
		)
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoaderPreview() {
	LoaderPage(title = "Loading...")
}

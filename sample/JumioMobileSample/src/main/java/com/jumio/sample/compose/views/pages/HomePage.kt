// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.views.atoms.PrimaryButton
import com.jumio.sample.compose.views.atoms.ToggleButtonGroup
import com.jumio.sample.compose.views.atoms.ToggleButtonState

/**
 * A page to enter the token for the workflow with option to select the data center and
 * start Custom UI or Default UI
 */
@Composable
fun HomePage(
	modifier: Modifier,
	onCustomUiClick: (String, String) -> Unit,
	onDefaultUiClick: (String, String) -> Unit,
) {
	var token by remember {
		mutableStateOf(TextFieldValue(""))
	}
	val datacenter = listOf(
		ToggleButtonState("US", true),
		ToggleButtonState("EU", false),
		ToggleButtonState("SG", false)
	)
	var selectedDatacenter by remember {
		mutableStateOf(datacenter[0].name)
	}

	Column(
		modifier = modifier
			.padding(MaterialTheme.spacing.medium),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TextField(
			value = token,
			onValueChange = {
				token = it
			},
			label = {
				Text(text = stringResource(id = R.string.token))
			},
			placeholder = {
				Text(text = stringResource(id = R.string.enter_token))
			},
			trailingIcon = {
				if (token.text.isNotEmpty()) {
					IconButton(onClick = { token = TextFieldValue("") }) {
						Icon(Icons.Default.Clear, contentDescription = "clear token")
					}
				}
			},
			singleLine = true,
			modifier = Modifier.fillMaxWidth(),
			colors = TextFieldDefaults.colors().copy(
				cursorColor = MaterialTheme.colors.primary,
				focusedIndicatorColor = MaterialTheme.colors.primary,
				unfocusedIndicatorColor = MaterialTheme.colors.unFocusedIndicator,
				focusedPlaceholderColor = MaterialTheme.colors.primary,
				unfocusedPlaceholderColor = MaterialTheme.colors.unFocusedIndicator,
				unfocusedTextColor = MaterialTheme.colors.unFocusedIndicator,
				focusedTextColor = MaterialTheme.colors.unFocusedIndicator,
				focusedTrailingIconColor = MaterialTheme.colors.primary,
				unfocusedTrailingIconColor = MaterialTheme.colors.unFocusedIndicator,
				focusedLabelColor = MaterialTheme.colors.primary,
				unfocusedLabelColor = MaterialTheme.colors.unFocusedIndicator
			)
		)
		ToggleButtonGroup(
			items = datacenter,
			onSelected = {
				selectedDatacenter = it
			}
		)
		PrimaryButton(
			title = stringResource(id = R.string.start_custom_ui_title).uppercase(),
			modifier = Modifier.fillMaxWidth()
		) {
			onCustomUiClick(token.text, selectedDatacenter)
		}
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
		PrimaryButton(
			title = stringResource(id = R.string.start_default_ui_title).uppercase(),
			modifier = Modifier.fillMaxWidth()
		) {
			onDefaultUiClick(token.text, selectedDatacenter)
		}
	}
}

@Preview
@Composable
fun HomePagePreview() {
	HomePage(
		modifier = Modifier,
		onCustomUiClick = { _, _ -> },
		onDefaultUiClick = { _, _ -> }
	)
}

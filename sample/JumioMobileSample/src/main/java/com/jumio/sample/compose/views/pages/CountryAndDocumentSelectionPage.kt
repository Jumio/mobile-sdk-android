// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumio.sample.R
import com.jumio.sample.compose.extension.getName
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sample.compose.viewModel.CustomUIEvent
import com.jumio.sample.compose.viewModel.CustomUIViewModel
import com.jumio.sdk.document.JumioDocument

/**
 * Shows the list of countries and documents. Selecting each country will show the list of documents for that country.
 * and selecting the document would start the first scanPart
 *
 */
@Composable
fun CountryAndDocumentSelectionPage(viewModel: CustomUIViewModel, modifier: Modifier = Modifier, onClose: () -> Unit) {
	val documentList by viewModel.documentList.collectAsStateWithLifecycle()
	Column(modifier = modifier.fillMaxSize()) {
		IconButton(onClick = {
			onClose()
		}) {
			Icon(
				imageVector = Icons.Default.Clear,
				contentDescription = stringResource(id = R.string.close),
				tint = MaterialTheme.colors.primary
			)
		}
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
		SelectCountry(
			countryList = viewModel.countryList,
			viewModel.selectedCountry,
			modifier = Modifier.weight(1f).padding(horizontal = MaterialTheme.spacing.medium)
		) {
			viewModel.onUiEvent(CustomUIEvent.CountrySelected(it))
		}
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
		SelectDocument(
			documentList = documentList,
			modifier = Modifier.weight(1f).padding(horizontal = MaterialTheme.spacing.medium)
		) {
			viewModel.onUiEvent(CustomUIEvent.DocumentSelected(it))
		}
	}
}

@Composable
fun SelectCountry(
	countryList: List<String>,
	default: String,
	modifier: Modifier = Modifier,
	onSelect: (String) -> Unit,
) {
	val defaultCountry = rememberSaveable { mutableStateOf(default) }
	Column(modifier = modifier) {
		Text(
			text = stringResource(id = R.string.select_country),
			color = MaterialTheme.colors.label
		)
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
		LazyColumn(modifier = Modifier.padding(start = MaterialTheme.spacing.medium)) {
			items(countryList) {
				Column {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.padding(
								top = MaterialTheme.spacing.small,
								bottom = MaterialTheme.spacing.small,
								end = MaterialTheme.spacing.medium
							)
							.clickable {
								onSelect(it)
								defaultCountry.value = it
							}
					) {
						Text(
							text = it,
							color = MaterialTheme.colors.label,
							modifier = Modifier.weight(1f)
						)
						if (defaultCountry.value == it) {
							Icon(
								imageVector = Icons.Filled.Check,
								contentDescription = stringResource(id = R.string.select_country),
								tint = MaterialTheme.colors.primary
							)
						}
					}
					HorizontalDivider()
				}
			}
		}
	}
}

@Composable
fun SelectDocument(
	documentList: List<JumioDocument>,
	modifier: Modifier = Modifier,
	onSelect: (JumioDocument) -> Unit,
) {
	Column(modifier = modifier) {
		Text(
			text = stringResource(id = R.string.select_document),
			color = MaterialTheme.colors.label
		)
		Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
		LazyColumn(modifier = Modifier.padding(start = MaterialTheme.spacing.medium)) {
			items(documentList) {
				Column {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.padding(
								top = MaterialTheme.spacing.small,
								bottom = MaterialTheme.spacing.small,
								end = MaterialTheme.spacing.medium
							)
							.clickable {
								onSelect(it)
							}
					) {
						Text(
							text = it.getName(),
							color = MaterialTheme.colors.label,
							modifier = Modifier.weight(1f)
						)
						Icon(
							imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
							contentDescription = stringResource(id = R.string.select_document),
							tint = MaterialTheme.colors.primary
						)
					}
					HorizontalDivider()
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun SelectCountryPreview() {
	SelectCountry(countryList = listOf("India", "USA", "UK"), "USA", onSelect = {})
}

@Preview(showBackground = true)
@Composable
fun SelectDocumentPreview() {
	SelectDocument(documentList = listOf(), onSelect = {})
}

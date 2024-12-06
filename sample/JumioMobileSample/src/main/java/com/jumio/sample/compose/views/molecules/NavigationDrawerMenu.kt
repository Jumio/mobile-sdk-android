// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.molecules

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.jumio.sample.R
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing
import com.jumio.sdk.JumioSDK

@Composable
fun NavigationDrawerMenu(openLink: (String) -> Unit, cleanModels: () -> Unit) {
	ModalDrawerSheet {
		Text(
			text = stringResource(id = R.string.menu_legal_information),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(MaterialTheme.spacing.medium)
		)
		NavigationDrawerItem(
			label = {
				Text(
					text = stringResource(id = R.string.menu_terms_of_use),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
			},
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.ic_info),
					contentDescription = stringResource(id = R.string.menu_terms_of_use),
					tint = MaterialTheme.colors.info
				)
			},
			selected = false,
			modifier = Modifier.padding(start = MaterialTheme.spacing.small),
			onClick = {
				openLink(
					"https://www.jumio.com/legal-information/privacy-policy/jumio-showcase-app-privacy-terms/"
				)
			}
		)
		NavigationDrawerItem(
			label = {
				Text(
					text = stringResource(id = R.string.menu_privacy_policy),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
			},
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.ic_info),
					contentDescription = stringResource(id = R.string.menu_privacy_policy),
					tint = MaterialTheme.colors.info
				)
			},
			selected = false,
			modifier = Modifier.padding(start = MaterialTheme.spacing.small),
			onClick = {
				openLink(
					"https://www.jumio.com/legal-information/privacy-policy/jumio-showcase-app-privacy-terms/"
				)
			}
		)
		NavigationDrawerItem(
			label = {
				Text(
					text = stringResource(id = R.string.menu_licenses),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
			},
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.ic_licenses),
					contentDescription = stringResource(id = R.string.menu_licenses),
					tint = Color.Black
				)
			},
			selected = false,
			modifier = Modifier.padding(start = MaterialTheme.spacing.small),
			onClick = {
				openLink("https://github.com/Jumio/mobile-sdk-android/tree/master/licenses")
			}
		)

		HorizontalDivider()

		Text(
			text = stringResource(id = R.string.menu_version),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(MaterialTheme.spacing.medium)
		)
		NavigationDrawerItem(
			label = {
				Text(text = JumioSDK.version, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
			},
			selected = false,
			modifier = Modifier.padding(start = MaterialTheme.spacing.small),
			onClick = {
				openLink("https://github.com/Jumio/mobile-sdk-android/releases")
			}
		)

		HorizontalDivider()

		Text(
			text = stringResource(id = R.string.menu_advanced),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(MaterialTheme.spacing.medium)
		)
		NavigationDrawerItem(
			label = {
				Text(
					text = stringResource(id = R.string.clean_models),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
			},
			selected = false,
			modifier = Modifier.padding(start = MaterialTheme.spacing.small),
			onClick = {
				cleanModels()
			}
		)
	}
}

// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.atoms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jumio.sample.compose.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, onNavigationIconClick: () -> Unit) {
	TopAppBar(
		title = {
			Text(text = title, style = MaterialTheme.typography.titleLarge)
		},
		navigationIcon = {
			IconButton(onClick = {
				onNavigationIconClick()
			}) {
				Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = Primary,
			titleContentColor = Color.White
		)
	)
}

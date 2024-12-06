// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.atoms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jumio.sample.compose.theme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(items: Array<String>, onSelected: (String) -> Unit) {
	var expanded by remember { mutableStateOf(false) }
	var selectedText by remember { mutableStateOf(items[0]) }

	Box(
		modifier = Modifier
			.fillMaxWidth()
	) {
		ExposedDropdownMenuBox(
			expanded = expanded,
			onExpandedChange = {
				expanded = !expanded
			},
			modifier = Modifier.fillMaxWidth()
		) {
			TextField(
				value = selectedText,
				onValueChange = {},
				readOnly = true,
				trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
				modifier = Modifier.menuAnchor().fillMaxWidth(),
				colors = TextFieldDefaults.colors().copy(
					cursorColor = MaterialTheme.colors.primary,
					focusedIndicatorColor = MaterialTheme.colors.primary,
					unfocusedIndicatorColor = MaterialTheme.colors.unFocusedIndicator,
					focusedPlaceholderColor = MaterialTheme.colors.primary,
					unfocusedPlaceholderColor = MaterialTheme.colors.unFocusedIndicator,
					unfocusedTextColor = MaterialTheme.colors.unFocusedIndicator,
					focusedTextColor = MaterialTheme.colors.unFocusedIndicator,
					focusedTrailingIconColor = MaterialTheme.colors.primary,
					unfocusedTrailingIconColor = MaterialTheme.colors.unFocusedIndicator
				)
			)

			ExposedDropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false }
			) {
				items.forEach { item ->
					DropdownMenuItem(
						text = { Text(text = item) },
						onClick = {
							selectedText = item
							expanded = false
							onSelected(item)
						}
					)
				}
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DropDownMenu_Preview() {
	DropDownMenu(items = arrayOf("Item 1", "Item 2", "Item 3"), onSelected = {})
}

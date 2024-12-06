// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.views.atoms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumio.sample.compose.theme.Primary
import com.jumio.sample.compose.theme.colors
import com.jumio.sample.compose.theme.spacing

@Composable
fun PrimaryButton(title: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
	Button(
		onClick = {
			onClick()
		},
		modifier = modifier,
		colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
		enabled = enabled
	) {
		Text(text = title, style = MaterialTheme.typography.titleMedium)
	}	
}

@Preview
@Composable
fun PrimaryButtonPreview() {
	PrimaryButton(
		title = "Start Custom UI",
		onClick = { }
	)
}

data class ToggleButtonState(val name: String, val isEnabled: Boolean)

@Composable
fun ToggleButtonGroup(items: List<ToggleButtonState>, onSelected: (String) -> Unit) {
	val toggleButtonList = remember {
		mutableStateListOf(
			*items.toTypedArray()
		)
	}

	Column(
		verticalArrangement = Arrangement.Center,
		modifier = Modifier.padding(MaterialTheme.spacing.medium)
	) {
		Row {
			toggleButtonList.forEachIndexed { index, item ->
				FilterChip(
					selected = item.isEnabled,
					onClick = {
						toggleButtonList.forEachIndexed { i, toggleButtonState ->
							toggleButtonList[i] = toggleButtonState.copy(isEnabled = i == index)
						}
						if (toggleButtonList[index].isEnabled) {
							onSelected(item.name)
						}
					},
					label = {
						Text(
							text = item.name,
							fontSize = 14.sp
						)
					},
					shape = when (index) {
						0 -> RoundedCornerShape(topStart = MaterialTheme.spacing.medium, bottomStart = MaterialTheme.spacing.medium)
						toggleButtonList.size - 1 -> RoundedCornerShape(
							topEnd = MaterialTheme.spacing.medium,
							bottomEnd = MaterialTheme.spacing.medium
						)
						else -> RoundedCornerShape(0.dp)
					},
					colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
						selectedContainerColor = Primary,
						selectedLabelColor = Color.White,
						containerColor = MaterialTheme.colors.background,
						labelColor = MaterialTheme.colors.label
					)
				)
			}
		}
	}
}

@Preview
@Composable
fun ToggleButtonGroupPreview() {
	ToggleButtonGroup(
		items = listOf(
			ToggleButtonState("US", true),
			ToggleButtonState("EU", false),
			ToggleButtonState("SG", false)
		),
		onSelected = {}
	)
}

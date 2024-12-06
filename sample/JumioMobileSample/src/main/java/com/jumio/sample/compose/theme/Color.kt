// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF2ABC6D)
val PrimaryDark = Color(0xFF008B41)
val Secondary = Color(0xFFEDF1F5)

data class CustomColors(
	val primary: Color = Color.Unspecified,
	val primaryDark: Color = Color.Unspecified,
	val secondary: Color = Color.Unspecified,
	val info: Color = Color.Unspecified,
	val label: Color = Color.Unspecified,
	val unFocusedIndicator: Color = Color.Unspecified,
	val background: Color = Color.Unspecified,
	val checkBox: Color = Color.Unspecified,
	val error: Color = Color.Unspecified,
)

val lightCustomColors = CustomColors(
	primary = Color(0xFF2ABC6D),
	primaryDark = Color(0xFF008B41),
	secondary = Color(0xFFEDF1F5),
	info = Color(0xFF303F9F),
	label = Color(0xFF000000),
	unFocusedIndicator = Color(0xFF000000),
	background = Color(0xFFFFFFFF),
	checkBox = Color(0xFFFFFFFF),
	error = Color(0xFFFF5722)
)

val darkCustomColors = CustomColors(
	primary = Color(0xFF2ABC6D),
	primaryDark = Color(0xFF008B41),
	secondary = Color(0xFFEDF1F5),
	info = Color(0xFF303F9F),
	label = Color(0xFFFFFFFF),
	unFocusedIndicator = Color(0xFFFFFFFF),
	background = Color(0xFF000000),
	checkBox = Color(0xFF000000),
	error = Color(0xFFFF5722)
)

val LocalCustomColors = compositionLocalOf { CustomColors() }

val MaterialTheme.colors: CustomColors
	@Composable
	@ReadOnlyComposable
	get() = LocalCustomColors.current

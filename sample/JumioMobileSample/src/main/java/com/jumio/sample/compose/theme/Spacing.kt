// Copyright 2024 Jumio Corporation, all rights reserved.
package com.jumio.sample.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
	val default: Dp = 0.dp,
	val ultraSmall: Dp = 2.dp,
	val extraSmall: Dp = 4.dp,
	val superSmall: Dp = 6.dp,
	val small: Dp = 8.dp,
	val compact: Dp = 12.dp,
	val medium: Dp = 16.dp,
	val semiLarge: Dp = 24.dp,
	val large: Dp = 32.dp,
	val superLarge: Dp = 48.dp,
	val extraLarge: Dp = 64.dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
	@Composable
	@ReadOnlyComposable
	get() = LocalSpacing.current

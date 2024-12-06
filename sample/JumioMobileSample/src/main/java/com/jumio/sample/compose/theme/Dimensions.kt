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
data class Dimensions(
	val buttonSize: Dp = Dp.Unspecified,
	val animationViewSize: Dp = Dp.Unspecified,
)

val customDimensions = Dimensions(
	buttonSize = 56.dp,
	animationViewSize = 340.dp
)

val LocalDimensions = compositionLocalOf { Dimensions() }

val MaterialTheme.dimensions: Dimensions
	@Composable
	@ReadOnlyComposable
	get() = LocalDimensions.current

package com.jumio.sample.compose.views

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param c
 * Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun Int.dpToPx(c: Context): Int =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), c.resources.displayMetrics).roundToInt()
